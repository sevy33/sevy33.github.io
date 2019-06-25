const axios = require("axios");

const options2 = {
  url: "https://www.macu.com/loans/home-loans/conventional-mortgage",
  method: "GET"
};
module.exports = ({ getDom, sleep, dbConnect }) => async () => {
  const record = {};
  try {
    const { data: html } = await axios(options2);
    const dom = await getDom(html);
    await sleep(3000);
    const table15 = dom.window.document.getElementById(
      "ctl00_plcMain_plcZones_lt_zoneTwoColumnPromoA_FlexPromo_4Column_Empty_FlexPromo_4Column_Empty_zone_1_RatesPromoSquare1_divBottomContent"
    );
    console.log(table15);
    const rate15 = table15.getElementsByClassName("rate-number")[0].innerHTML;
    record["15"] = rate15;

    console.log(record["15"]);

    const table30 = dom.window.document.getElementById(
      "ctl00_plcMain_plcZones_lt_zoneTwoColumnPromoA_FlexPromo_4Column_Empty_FlexPromo_4Column_Empty_zone_0_RatesPromoSquare_divBottomContent"
    );
    const rate30 = table30.getElementsByClassName("rate-number")[0].innerHTML;
    record["30"] = rate30;

    console.log(record);
    dbConnect(record, 2);
    return record;
  } catch (e) {
    console.log("error");
  }
};
