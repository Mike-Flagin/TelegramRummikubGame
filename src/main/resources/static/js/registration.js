let stompClient = null;
let params = new URLSearchParams(document.location.search);
let gameId = params.get("gameId");
let userId = params.get("userId");
let leaving = false;

function connect() {
  let socket = new SockJS("/ws");
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    stompClient.subscribe("/registration/updates/" + gameId, (update) =>
      updatePage(JSON.parse(update.body))
    );

    stompClient.subscribe("/registration/start/" + gameId, () => startGame());
  });
}

function updatePage(update) {
  if (update.add) addUser(update.user);
  else removeUser(update.user);
}

function addUser(user) {
  let table = document
    .getElementById("user-table")
    .getElementsByTagName("tbody")[0];
  let newRow = table.insertRow();
  newRow.id = user.userId;

  let cell = newRow.insertCell(0);
  cell.classList.add("user-item");

  let img = document.createElement("img");
  img.src = "/images" + user.image;

  var span = document.createElement("span");
  span.textContent = user.username;

  cell.appendChild(img);
  cell.appendChild(span);
}

function removeUser(user) {
  document.getElementById(user.userId).remove();
}

function leave() {
  stompClient.send(
    "/app/registration/leave",
    {},
    JSON.stringify({ add: false, user: { userId: userId, gameId: gameId } })
  );
  leaving = true;
  window.close();
}

function start() {
  stompClient.send("/app/registration/start", {}, gameId);
}

function startGame() {
  leaving = true;
  let url =
    window.location.origin + "/game?gameId=" + gameId + "&userId=" + userId;
  window.location.replace(url);
}

window.onbeforeunload = (event) => {
  if (!leaving) event.preventDefault();
};

window.onload = function () {
  connect();
};
