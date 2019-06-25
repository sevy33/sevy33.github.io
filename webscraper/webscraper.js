const express = require("express");
const app = express();
const PORT = 8000;
const pg = require("pg");
const path = require("path");
const { Promise } = require("bluebird");
const getHTML = require("./banks/afirst");
const getHTML2 = require("./banks/mwest");
const getHTML3 = require("./banks/gwest");

const config = {
  user: "postgres",
  database: "WebScrape",
  password: "Testdb33",
  port: 5432
};

const pool = new pg.Pool(config);

const { JSDOM } = require("jsdom");

const sleep = (time = 0) => {
  return new Promise((resolve, reject) => {
    setTimeout(resolve, time);
  });
};

const getDom = async html => {
  const dom = await new JSDOM(html);
  console.log(dom.window.document.body);
  return dom;
};

const dbConnect = (htmlData, id) => {
  console.log("i made it here");
  const data15 = htmlData["15"].substring(0, htmlData["15"].length - 1);
  const data30 = htmlData["30"].substring(0, htmlData["30"].length - 1);
  console.log(data15);
  const qres = {};
  pool.query(
    "SELECT fifteen_year, thirty_year FROM banks where banks_id = $1",
    [id],
    (error, results) => {
      if (error) {
        throw error;
      }
      qres["15"] = parseFloat(results.rows[0].fifteen_year).toFixed(4);
      qres["30"] = parseFloat(results.rows[0].thirty_year).toFixed(4);
      pool.query(
        "Update banks set fifteen_year = $1, thirty_year = $2, old_f_year = $3, old_t_year = $4 where banks_id = $5",
        [data15, data30, qres["15"], qres["30"], id],
        (error, results) => {
          if (error) {
            throw error;
          }
          console.log("query successfull");
        }
      );
    }
  );
};

const getAllHtml = async (request, response) => {
  const record = {};
  try {
    const [one, two, three] = await Promise.map(
      [
        getHTML({ getDom, sleep, dbConnect }),
        getHTML2({ getDom, sleep, dbConnect }),
        getHTML3({ getDom, sleep, dbConnect })
      ],
      fn => fn()
    );
    record["1"] = one;
    record["2"] = two;
    record["3"] = three;
    return response.json(record);
  } catch (e) {
    console.log(e);
  }
};

const getPrevData = (request, response) => {
  console.log("i made it get");
  pool.query(
    "SELECT old_f_year, old_t_year FROM banks ORDER BY banks_id ASC",
    (error, results) => {
      if (error) {
        throw error;
      }
      return response.status(200).json(results.rows);
    }
  );
};

app.get("/data", getAllHtml);
app.get("/prevdata", getPrevData);
app.get("/", (req, res) =>
  res.sendFile(path.join(__dirname, "./client/build/index.html"))
);

app.use(express.static(path.join(__dirname, "./client/build")));

app.listen(PORT, () => console.log(`(server): listening on port ${PORT}...`));
