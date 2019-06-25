"""A naive passthrough FUSE filesystem.
Note, the documentation present here is documentation for the C FUSE API, and
may not correspond 1-to-1 with the Python FUSE API. It is included for
informational purposes only to highlight the intended purpose of each method.
This is part of the example code from the fusepy library.
It's the python language wrapper around libFUSE, the "filesystems in userspace" library
You'll need to modify the constructor (__init__), open(), create(),
read(), write(), truncate(), and release() methods.  See the assignment description for
what to do
"""

from __future__ import with_statement

from functools import wraps
import os
import sys
import errno
import logging

from fuse import FUSE, FuseOSError, Operations
import inspect


import base64
from cryptography.fernet import Fernet
from cryptography.hazmat.backends import default_backend
from cryptography.hazmat.primitives import hashes
from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC

import getpass

log = logging.getLogger(__name__)


def logged(f):
    @wraps(f)
    def wrapped(*args, **kwargs):
        log.info('%s(%s)', f.__name__, ','.join(
            [str(item) for item in args[1:]]))
        return f(*args, **kwargs)
    return wrapped


class EncFS(Operations):
    """A simple passthrough interface.
    Initialize the filesystem. This function can often be left unimplemented, but
    it can be a handy way to perform one-time setup such as allocating
    variable-sized data structures or initializing a new filesystem. The
    fuse_conn_info structure gives information about what features are supported
    by FUSE, and can be used to request certain capabilities (see below for more
    information). The return value of this function is available to all file
    operations in the private_data field of fuse_context. It is also passed as a
    parameter to the destroy() method.
    """

    def __init__(self, root):
        self.root = root
        # TODO, FINISH ME!!
        self.password = getpass.getpass(prompt='Password: ', stream=None)
        self.rsalt = os.urandom(16)
        self.fd = 0
        self.dict = dict()

    def destroy(self, path):
        """Clean up any resources used by the filesystem.

        Called when the filesystem exits.

        """
        pass

    # NOTE THIS MIGHT BE USEFUL IN SEVERAL PLACES!
    def _full_path(self, partial):
        """Calculate full path for the mounted file system.
          .. note::
            This isn't the same as the full path for the underlying file system.
            As such, you can't use os.path.abspath to calculate this, as that
            won't be relative to the mount point root.
        """
        if partial.startswith("/"):
            partial = partial[1:]
        path = os.path.join(self.root, partial)
        return path

    @logged
    def access(self, path, mode):
        """Access a file.
        This is the same as the access(2) system call. It returns -ENOENT if
        the path doesn't exist, -EACCESS if the requested permission isn't
        available, or 0 for success. Note that it can be called on files,
        directories, or any other object that appears in the filesystem. This
        call is not required but is highly recommended.
        """
        full_path = self._full_path(path)
        if not os.access(full_path, mode):
            raise FuseOSError(errno.EACCES)

    @logged
    def chmod(self, path, mode):
        """Change a file's permissions.
        Change the mode (permissions) of the given object to the given new
        permissions. Only the permissions bits of mode should be examined. See
        chmod(2) for details.
        """
        full_path = self._full_path(path)
        return os.chmod(full_path, mode)

    @logged
    def chown(self, path, uid, gid):
        """Change a file's owernship.
        Change the given object's owner and group to the provided values. See
        chown(2) for details. NOTE: FUSE doesn't deal particularly well with
        file ownership, since it usually runs as an unprivileged user and this
        call is restricted to the superuser. It's often easier to pretend that
        all files are owned by the user who mounted the filesystem, and to skip
        implementing this function.
        """
        full_path = self._full_path(path)
        return os.chown(full_path, uid, gid)

    @logged
    def getattr(self, path, fh=None):
        """Return file attributes.
        The "stat" structure is described in detail in the stat(2) manual page.
        For the given pathname, this should fill in the elements of the "stat"
        structure. If a field is meaningless or semi-meaningless (e.g., st_ino)
        then it should be set to 0 or given a "reasonable" value. This call is
        pretty much required for a usable filesystem.
        """
        full_path = self._full_path(path)
        st = os.lstat(full_path)
        return dict((key, getattr(st, key)) for key in ('st_atime', 'st_ctime',
                                                        'st_gid', 'st_mode', 'st_mtime', 'st_nlink', 'st_size', 'st_uid'))

    @logged
    def readdir(self, path, fh):
        """Read a directory.
        Return one or more directory entries (struct dirent) to the caller.
        This is one of the most complex FUSE functions. It is related to, but
        not identical to, the readdir(2) and getdents(2) system calls, and the
        readdir(3) library function. Because of its complexity, it is described
        separately below. Required for essentially any filesystem, since it's
        what makes ls and a whole bunch of other things work.
        """
        full_path = self._full_path(path)

        dirents = ['.', '..']
        if os.path.isdir(full_path):
            dirents.extend(os.listdir(full_path))
        for r in dirents:
            yield r

    @logged
    def readlink(self, path):
        """Read a symbolic link.
        If path is a symbolic link, fill buf with its target, up to size. See
        readlink(2) for how to handle a too-small buffer and for error codes.
        Not required if you don't support symbolic links. NOTE: Symbolic-link
        support requires only readlink and symlink. FUSE itself will take care
        of tracking symbolic links in paths, so your path-evaluation code
        doesn't need to worry about it.
        """
        pathname = os.readlink(self._full_path(path))
        if pathname.startswith("/"):
            # Path name is absolute, sanitize it.
            return os.path.relpath(pathname, self.root)
        else:
            return pathname

        # don't allow mknod
    '''@logged
    def mknod(self, path, mode, dev):
        """Make a special file.
        Make a special (device) file, FIFO, or socket. See mknod(2) for
        details. This function is rarely needed, since it's uncommon to make
        these objects inside special-purpose filesystems.
        """
        return os.mknod(self._full_path(path), mode, dev)
'''
    @logged
    def rmdir(self, path):
        """Remove a directory.
        Remove the given directory. This should succeed only if the directory
        is empty (except for "." and ".."). See rmdir(2) for details.
        """
        full_path = self._full_path(path)
        return os.rmdir(full_path)

    @logged
    def mkdir(self, path, mode):
        """Make a directory.
        Create a directory with the given name. The directory permissions are
        encoded in mode. See mkdir(2) for details. This function is needed for
        any reasonable read/write filesystem.
        """
        return os.mkdir(self._full_path(path), mode)

    @logged
    def statfs(self, path):
        full_path = self._full_path(path)
        stv = os.statvfs(full_path)
        return dict((key, getattr(stv, key)) for key in ('f_bavail', 'f_bfree',
                                                         'f_blocks', 'f_bsize', 'f_favail', 'f_ffree', 'f_files', 'f_flag',
                                                         'f_frsize', 'f_namemax'))

    @logged
    def unlink(self, path):
        """Unlink a file.
        Remove (delete) the given file, symbolic link, hard link, or special
        node. Note that if you support hard links, unlink only deletes the data
        when the last hard link is removed. See unlink(2) for details.
        """
        return os.unlink(self._full_path(path))

    # no symlinks
    '''    @logged
    def symlink(self, target, name):
        """Create a symbolic link.
        Create a symbolic link named "from" which, when evaluated, will lead to
        "to". Not required if you don't support symbolic links. NOTE:
        Symbolic-link support requires only readlink and symlink. FUSE itself
        will take care of tracking symbolic links in paths, so your
        path-evaluation code doesn't need to worry about it.
        """
        return os.symlink(self._full_path(target), self._full_path(name))
    '''
    @logged
    def rename(self, old, new):
        """Rename a file.
        Rename the file, directory, or other object "from" to the target "to".
        Note that the source and target don't have to be in the same directory,
        so it may be necessary to move the source to an entirely new directory.
        See rename(2) for full details.
        """
        return os.rename(self._full_path(old), self._full_path(new))
# no hard links either
    '''
    @logged
    def link(self, target, name):
        """Create a hard link.
        Create a hard link between "from" and "to". Hard links aren't required
        for a working filesystem, and many successful filesystems don't support
        them. If you do implement hard links, be aware that they have an effect
        on how unlink works. See link(2) for details.
        """
        return os.link(self._full_path(target), self._full_path(name))
'''
    @logged
    def utimens(self, path, times=None):
        return os.utime(self._full_path(path), times)

    @logged
    def open(self, path, flags):
        """Open a file.
        Open a file. If you aren't using file handles, this function should
        just check for existence and permissions and return either success or
        an error code. If you use file handles, you should also allocate any
        necessary structures and set fi->fh. In addition, fi has some other
        fields that an advanced filesystem might find useful; see the structure
        definition in fuse_common.h for very brief commentary.
        """
        open_path = self._full_path(path)
        if not os.path.exists(open_path):
            print('error opening file')
            return -1
        if not os.access(open_path, flags):
            print('no permissions')
            return -1
        if open_path in self.dict:
            print(file is open)
            return -1
        fd = os.open(open_path, flags)
        if fd < 0:
            print('error opening file')

        salt = os.read(fd, 16)
        input = fd.read()
        kdf = PBKDF2HMAC(algorithm=hashes.SHA256(), length=32,
                         salt=salt, iterations=100000, backend=default_backend())
        key = base64.urlsafe_b64encode(kdf.derive(self.password))
        f = Fernet(key)
        decr = f.decrypt(input)
        self.dict[open_path] = decr
        fd.close()
        self.fd += 1
        return self.fd

    @logged
    def create(self, path, mode, fi=None):
        create_path = self._full_path(path)
        if os.path.exists(create_path):
            print('file exists')
            return -1
        fd = os.open(create_path, os.O_RDWR | os.O_CREAT, mode)
        if fd < 0:
            print('error opening file')
        self.dict[create_path] = bytes()
        self.fd += 1
        fd.close()
        return self.fd

    @logged
    def read(self, path, length, offset, fh):
        """Read from a file.
        Read size bytes from the given file into the buffer buf, beginning
        offset bytes into the file. See read(2) for full details. Returns the
        number of bytes transferred, or 0 if offset was at or beyond the end of
        the file. Required for any sensible filesystem.
        """
        read_path = self._full_path(path)
        if not read_path in self.dict:
            return 0
        buf = self.dict[path]
        return buf[offset:offset + length]

    @logged
    def write(self, path, buf, offset, fh):
        """Write to a file.
        """
        write_path = self._full_path(path)
        if not write_path in self.dict:
            return 0
        buf_begin = self.dict[path]
        buf_begin = self.dict[:offset]
        buf_end = self.dict[path]
        buf_end = self.dict[offset:]
        self.dict[path] = buf_begin + buf + buf_end
        return len(buf)

    @logged
    def truncate(self, path, length, fh=None):
        """Truncate a file.
        Truncate or extend the given file so that it is precisely size bytes
        long. See truncate(2) for details. This call is required for read/write
        filesystems, because recreating a file will first truncate it.
        """
        truncate_path = self._full_path(path)
        if not truncate_path in self.dict:
            return 0
        f_size = os.path.getsize(truncate_path)
        if f_size == length:
            return 0
        if f_size <= length:
            self.dict[path] = self.dict[path][:length]
        else:
            self.dict[path] += bytearray(length - f_size)
        return f_size

    # skip
    '''
    @logged
    def flush(self, path, fh):
        """Flush buffered information.
        Called on each close so that the filesystem has a chance to report
        delayed errors. Important: there may be more than one flush call for
        each open. Note: There is no guarantee that flush will ever be called
        at all!
        """
        return os.fsync(fh)
   '''

    @logged
    def release(self, path, fh):
        """Release is called when FUSE is done with a file.
        This is the only FUSE function that doesn't have a directly
        corresponding system call, although close(2) is related. Release is
        called when FUSE is completely done with a file; at that point, you can
        free up any temporarily allocated data structures. The IBM document
        claims that there is exactly one release per open, but I don't know if
        that is true.
        """
        release_path = self._full_path(path)
        if not release_path in self.dict:
            return 0
        salt = os.urandom(16)
        kdf = PBKDF2HMAC(algorithm=hashes.SHA256(), length=32,
                         salt=salt, iterations=100000, backend=default_backend())
        key = base64.urlsafe_b64encode(kdf.derive(self.password))
        f = Fernet(key)
        encr = f.encrypt(self.dict[path])
        self.fd -= 1
        del self.dict[release_path]
        fd = os.open(release_path, os.O_WRONLY)
        fd.write(salt + encr)
        fd.close
        return 0


    # skip
'''    @logged
    def fsync(self, path, fdatasync, fh):
        """Flush any dirty information to disk.
        Flush any dirty information about the file to disk. If isdatasync is
        nonzero, only data, not metadata, needs to be flushed. When this call
        returns, all file data should be on stable storage. Many filesystems
        leave this call unimplemented, although technically that's a Bad Thing
        since it risks losing data. If you store your filesystem inside a plain
        file on another filesystem, you can implement this by calling fsync(2)
        on that file, which will flush too much data (slowing performance) but
        achieve the desired guarantee.
        """
        return self.flush(path, fh)
'''
if __name__ == '__main__':
    from sys import argv
    if len(argv) != 3:
        print('usage: %s <encrypted folder> <mountpoint>' % argv[0])
        exit(1)

    logging.basicConfig(level=logging.DEBUG)
    # create our virtual filesystem using argv[1] as the physical filesystem
    # and argv[2] as the virtual filesystem
    fuse = FUSE(EncFS(argv[1]), argv[2], foreground=True)
