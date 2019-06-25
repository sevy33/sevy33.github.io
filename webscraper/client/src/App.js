import React, { useState, useEffect } from "react";
import "./App.css";
import Email from "./Email";

const App = () => {
  const initialRatesState = {
    "1": { "15": null, "30": null },
    "2": { "15": null, "30": null },
    "3": { "15": null, "30": null }
  };
  // const initialRatesState = { id: null, f_year: "", t_year: "" };
  const [rates, setRates] = useState(initialRatesState);
  const [appTableShowing, setAppTableShowing] = useState(false);

  const changeShowState = show => {
    if (appTableShowing) setAppTableShowing(false);
    else setAppTableShowing(true);
  };

  const getData = async () => {
    try {
      const response = await fetch("/data");
      const data = await response.json();
      console.log(data);
      setRates(oldData => ({ ...oldData, ...data }));
    } catch (e) {
      console.log("err", e);
    }
  };
  useEffect(() => {
    getData();
    return () => {};
  }, []);

  const content = appTableShowing ? <Email props={appTableShowing} /> : null;

  return (
    <div className="App">
      <div className="App-header">
        <div className="grid-container">
          <div className="spacing">Bank</div>
          <div className="spacing">15 Year Fixed</div>
          <div className="spacing">30 Year Fixed</div>
        </div>
        <div className="grid-container">
          <div className="spacing">America First</div>
          <div className="spacing">{rates["1"]["15"]}</div>
          <div className="spacing">{rates["1"]["30"]}</div>
        </div>
        <div className="grid-container">
          <div className="spacing">Mountain America</div>
          <div className="spacing">{rates["2"]["15"]}</div>
          <div className="spacing">{rates["2"]["30"]}</div>
        </div>
        <div className="grid-container">
          <div className="spacing">Utah First</div>
          <div className="spacing">{rates["3"]["15"]}</div>
          <div className="spacing">{rates["3"]["30"]}</div>
        </div>
        <button onClick={changeShowState}>See Previous data</button>
        <div>{content}</div>
      </div>
    </div>
  );
};

export default App;
