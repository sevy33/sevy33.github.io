import React, { useState } from "react";
import axios from "axios";

const AddUserForm = props => {
  const initialFormState = { id: null, first_name: "", username: "" };
  const [user, setUser] = useState(initialFormState);

  const handleInputChange = event => {
    const { name, value } = event.target;

    setUser({ ...user, [name]: value });
  };

  return (
    <form
      onSubmit={event => {
        event.preventDefault();
        if (!user.first_name || !user.username) return;
        const options = {
          method: "POST",
          url: "/postusers",
          headers: {
            "Content-Type": "application/json"
          },
          data: user
        };
        axios(options).then(res => {
          console.log(res);
          console.log(res.data);
        });
        props.addUser(user);
        setUser(initialFormState);
      }}
    >
      <label>Name</label>
      <input
        type="text"
        name="first_name"
        value={user.first_name}
        onChange={handleInputChange}
      />
      <label>Username</label>
      <input
        type="text"
        name="username"
        value={user.username}
        onChange={handleInputChange}
      />
      <button>Add new user</button>
    </form>
  );
};

export default AddUserForm;
