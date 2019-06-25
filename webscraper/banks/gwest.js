const axios = require("axios");

const options3 = {
  url: "https://utahfirst.com/home-mortgages-2/",
  method: "GET"
};
// const getData = (_, element) => (element ? element.childre[0].data.trim() : "");
// const getWindowLoad = ({ window }) =>
//   new Promise(resolve => {
//     window.onload = () => resolve(window);
//   });
// const getDom = async () => {
//   const { data: html } = await axios(options3);
//   const $ = cheerio.load(html);
//   // setTimeout(5000);
//   console.log("waiting?");
//   $(window).load = () => {
//     console.log("loaded");
//     const thing = $(".ratesection");
//     console.log(thing);
//     const other_thing = thing.map(getData).get();
//     console.log(other_thing);
//   };
// };
module.exports = ({ getDom, sleep, dbConnect }) => async () => {
  const record = {};
  try {
    const { data: html } = await axios(options3);
    const dom = await getDom(html);
    await sleep(3000);
    // const window = await getWindowLoad(dom);
    // console.log(window.document.body);
    // await sleep(3000);
    //const main = dom.window.document.getElementById("main-content");
    const secdiv = dom.window.document.getElementsByClassName(
      "trow js-calc-row"
    );
    const tcell = [...secdiv].filter((x, i) => i === 1)[0];
    record["15"] = tcell.getElementsByClassName("highlight")[0].innerHTML;

    const tcell2 = [...secdiv].filter((x, i) => i === 2)[0];
    record["30"] = tcell2.getElementsByClassName("highlight")[0].innerHTML;

    console.log(record);
    dbConnect(record, 3);
    return record;
  } catch (e) {
    console.log(e);
  }
};
