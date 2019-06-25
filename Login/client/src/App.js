import React, { useState, Fragment, useEffect } from "react";
import Login from "./forms/Login";
import AppTableStuff from "./tablestuff/AppTableStuff";

const App = () => {
  const [appTableShowing, setAppTableShowing] = useState(false);

  const changeShowState = show => {
    setAppTableShowing(true);
  };

  const content = appTableShowing ? (
    <AppTableStuff />
  ) : (
    <Login changeShowState={changeShowState} />
  );
  return <div>{content}</div>;
};

export default App;
