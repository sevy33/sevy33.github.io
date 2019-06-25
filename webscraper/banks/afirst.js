const axios = require("axios");

const options = {
  url:
    "https://www.americafirst.com/loans/mortgage-loans/mortgage-loans.html?cid=web-ab-loans_mortgage_targeted-afcu-ac-ac-20190110%20",
  method: "GET"
};
module.exports = ({ getDom, sleep, dbConnect }) => async () => {
  const record = {};
  try {
    const { data: html } = await axios(options);
    const dom = await getDom(html);
    await sleep(3000);
    const table = dom.window.document.getElementsByClassName(
      "loan-rates-table"
    )[0];
    const trs = table.getElementsByTagName("tr");
    [...trs].forEach(tr => {
      const tds = tr.getElementsByTagName("td");
      [...tds].forEach((td, i) => {
        const val = td.innerHTML;
        if (val.includes("* Rates")) return;
        if (val.includes("Year Fixed")) {
          const year = val.split(" ")[0];
          record[year] = tds[i + 1].innerHTML;
        }
      });
    });
    console.log(record);
    dbConnect(record, 1);
    return record;
  } catch (e) {
    console.log(e);
  }
};
