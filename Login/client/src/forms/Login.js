import React, { useState } from "react";
import axios from "axios";

const Login = props => {
  const initialFormState = { id: null, first_name: "", username: "" };
  const [user, setUser] = useState(initialFormState);
  const { changeShowState } = props;

  const checkUser = event => {
    if (!user.first_name || !user.username) return;
    const options = {
      method: "POST",
      url: "/checkusers",
      headers: {
        "Content-Type": "application/json"
      },
      data: user
    };
    axios(options).then(res => {
      console.log(res.data);
      if (res.data == "passed") {
        console.log("yes");
        props.changeShowState(true);
        setUser(initialFormState);
      } else {
        console.log("no");
        alert("Not a User Yet!");
      }
    });
  };

  const handleInputChange = event => {
    const { name, value } = event.target;

    setUser({ ...user, [name]: value });
  };

  return (
    <div>
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
      <button onClick={checkUser}>Login</button>
    </div>
  );
};

export default Login;
