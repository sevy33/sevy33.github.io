import React, { useState, useEffect } from "react";

const Email = props => {
  const initialRatesState = {
    "0": { old_f_year: null, old_t_year: null },
    "1": { old_f_year: null, old_t_year: null },
    "2": { old_f_year: null, old_t_year: null }
  };
  const [rates, setRates] = useState(initialRatesState);
  const getData = async () => {
    console.log("made it");
    try {
      const response = await fetch("/prevdata");
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
  return (
    <div>
      <div className="grid-container">
        <div className="spacing">Bank</div>
        <div className="spacing">15 Year Fixed</div>
        <div className="spacing">30 Year Fixed</div>
      </div>
      <div className="grid-container">
        <div className="spacing">America First</div>
        <div className="spacing">{rates["0"]["old_f_year"]}</div>
        <div className="spacing">{rates["0"]["old_t_year"]}</div>
      </div>
      <div className="grid-container">
        <div className="spacing">Mountain America</div>
        <div className="spacing">{rates["1"]["old_f_year"]}</div>
        <div className="spacing">{rates["1"]["old_t_year"]}</div>
      </div>
      <div className="grid-container">
        <div className="spacing">Utah First</div>
        <div className="spacing">{rates["2"]["old_f_year"]}</div>
        <div className="spacing">{rates["2"]["old_t_year"]}</div>
      </div>
    </div>
  );
};

export default Email;
