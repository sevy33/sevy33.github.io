//
//  main.c
//  RandDeviceKernal
//
//  Created by Devin Sevy on 4/10/19.
//  Copyright © 2019 Devin Sevy. All rights reserved.
//

#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <linux/kernel.h>

int main(int argc, const char * argv[]) {
    int ret, fd;
    unsigned char c[10] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    fd = open("/dev/myRand", O_RDWR, S_IRWXU);
    if(fd < 0) perror("Error Opening");
    printk("file opened");
    write(fd, c, sizeof(c));
    read(fd, c, sizeof(c));
    return 0;
}
