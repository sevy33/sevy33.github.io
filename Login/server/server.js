const express = require("express");
const app = express();
const port = 8000;
const path = require("path");
const router = express.Router();
const pg = require("pg");
const bodyParser = require("body-parser");

const config = {
  user: "postgres",
  database: "Testdb",
  password: "Testdb33",
  port: 5432
};
app.use(bodyParser.json());
app.use(bodyParser.urlencoded());
// pool takes the object above -config- as parameter
const pool = new pg.Pool(config);

const getUsers = (request, response) => {
  console.log("i made it get");
  pool.query("SELECT * FROM users ORDER BY users_id ASC", (error, results) => {
    if (error) {
      throw error;
    }
    return response.status(200).json(results.rows);
  });
};

const postUsers = (request, response) => {
  console.log("i made it post");
  //console.log(request);
  //console.log(request.username);
  const { first_name, username } = request.body;
  pool.query(
    "insert into users(first_name, username) values($1, $2) returning *;",
    [first_name, username],
    (error, results) => {
      if (error) {
        throw error;
      }
      return response.status(200).json("User added with ID: ${result.id}");
    }
  );
};

const checkUsers = (request, response) => {
  console.log("i made it checkusers");
  const { first_name, username } = request.body;
  pool.query(
    "select first_name, username from users where first_name = $1 and username = $2;",
    [first_name, username],
    (error, results) => {
      if (error) {
        throw error;
      }
      console.log(results.rows);
      if (results.rowCount == 1) return response.status(200).json("passed");
      else response.status(200).json("failed");
    }
  );
};

app.get("/users", getUsers);
app.post("/postusers", postUsers);
app.post("/checkusers", checkUsers);
app.get("/users/:id", (req, res) => {
  console.log(req.params.id);
  return res.send(req.params);
});

app.use(express.static(path.join(__dirname, "../client/build")));

// on a GET request to host:8000/ simply respond with 'hi'
app.get("/", (req, res) =>
  res.sendFile(path.join(__dirname, "../client/build/index.html"))
);

// allow app to listen on port 8000 and console log your server running msg
app.listen(8000, () => console.log("app is listening on port 8000..."));

module.exports = {
  getUsers
};
