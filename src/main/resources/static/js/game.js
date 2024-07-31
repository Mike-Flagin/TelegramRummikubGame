let stompClient = null;
let params = new URLSearchParams(document.location.search);
let gameId = params.get("gameId");
let userId = params.get("userId");
let dragged = null;
let currentPlayer;
let update_;
let end = false;

function connect() {
  let socket = new SockJS("/ws");
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function (frame) {
    stompClient.send(
      "/app/game/getUpdates",
      {},
      JSON.stringify({ userId: userId, gameId: gameId })
    );
    stompClient.subscribe(
      "/game/updates/" + gameId + "/" + userId,
      (update) => {
        if (update.body == "gameUpdated") {
          saveBoard();
        } else if (update.body == "gameEnd") {
          endGame();
        } else {
          update_ = JSON.parse(update.body);
          updateGame(update_);
        }
      }
    );
  });
}

function updateGame(update) {
  currentPlayer = update.currentPlayer;
  drawPlayers(update.players);
  drawTable(update.table);
  drawBoard(update.tiles);
}

function drawTable(table) {
  if (table == null) {
    for (let block = 0; block < 3; block++) {
      let blocks = document
        .getElementsByClassName("four-block-tables")
        [block].getElementsByTagName("tbody")[0];
      for (let row = 0; row < 8; row++) {
        let rows = blocks.getElementsByClassName("four-block-lines")[row];
        for (let column = 0; column < 4; column++) {
          let tile = rows.cells[column];
          tile.innerHTML = "";
        }
      }
    }
    for (let rowCounter = 0; rowCounter < 8; rowCounter++) {
      let row = document
        .getElementsByClassName("thirteen-block-tables")[0]
        .getElementsByTagName("tbody")[0].children[rowCounter];
      for (let column = 0; column < 13; column++) {
        let tile = row.cells[column];
        tile.innerHTML = column + 1;
      }
    }
    return;
  }
  if (table.fourBlocks != null) {
    for (let block = 0; block < 3; block++) {
      let blocks = document
        .getElementsByClassName("four-block-tables")
        [block].getElementsByTagName("tbody")[0];
      for (let row = 0; row < 8; row++) {
        let rows = blocks.getElementsByClassName("four-block-lines")[row];
        for (let column = 0; column < 4; column++) {
          let tile = rows.cells[column];
          tile.innerHTML = "";
          if (table.fourBlocks[block][row][column] == null) {
            continue;
          }
          addTile(tile, table.fourBlocks[block][row][column]);
        }
      }
    }
  }
  if (table.thirteenBlocks != null) {
    for (let rowCounter = 0; rowCounter < 8; rowCounter++) {
      let row = document
        .getElementsByClassName("thirteen-block-tables")[0]
        .getElementsByTagName("tbody")[0].children[rowCounter];
      for (let column = 0; column < 13; column++) {
        let tile = row.cells[column];
        tile.innerHTML = column + 1;
        if (table.thirteenBlocks[rowCounter][column] == null) {
          continue;
        }
        addTile(tile, table.thirteenBlocks[rowCounter][column]);
      }
    }
  }
}

function drawPlayers(players) {
  let table = document
    .getElementById("players-table")
    .getElementsByTagName("tbody")[0];
  table.innerHTML = "";
  players.forEach((player) => {
    let newRow = table.insertRow();

    let cell = newRow.insertCell(0);
    cell.classList.add("player-item");
    if (currentPlayer == player.id) cell.classList.add("current-player");

    let img = document.createElement("img");
    img.src = "/images" + player.image;

    var span = document.createElement("span");
    span.textContent = player.username;

    cell.appendChild(img);
    cell.appendChild(span);
  });
}

function drawBoard(tiles) {
  let table = document
    .getElementById("player-tiles-table")
    .getElementsByTagName("tbody")[0];
  for (let i = 0; i < tiles.length; i++) {
    for (let j = 0; j < tiles[i].length; j++) {
      let tile = table.children[i].children[j];
      tile.innerHTML = "";
      if (tiles[i][j] == null) {
        continue;
      }
      addTile(tile, tiles[i][j]);
    }
  }
}

function addTile(td, tileToAdd) {
  let tile = td.appendChild(document.createElement("div"));
  tile.classList.add("tile");
  tile.draggable = true;
  tile.ondragstart = (event) => (dragged = event.target);
  tile.onclick = (event) => {
    let dr = document.getElementsByClassName("dragover");
    if (dr.length != 0) dr[0].classList.remove("dragover");
    dragged = event.target;
  };
  switch (tileToAdd.color) {
    case "RED":
      tile.classList.add("tile-red");
      break;
    case "ORANGE":
      tile.classList.add("tile-orange");
      break;
    case "BLACK":
      tile.classList.add("tile-black");
      break;
    case "BLUE":
      tile.classList.add("tile-blue");
      break;
  }

  switch (tileToAdd.number) {
    case "ONE":
      tile.textContent = "1";
      break;
    case "TWO":
      tile.textContent = "2";
      break;
    case "THREE":
      tile.textContent = "3";
      break;
    case "FOUR":
      tile.textContent = "4";
      break;
    case "FIVE":
      tile.textContent = "5";
      break;
    case "SIX":
      tile.textContent = "6";
      break;
    case "SEVEN":
      tile.textContent = "7";
      break;
    case "EIGHT":
      tile.textContent = "8";
      break;
    case "NINE":
      tile.textContent = "9";
      break;
    case "TEN":
      tile.textContent = "10";
      break;
    case "ELEVEN":
      tile.textContent = "11";
      break;
    case "TWELVE":
      tile.textContent = "12";
      break;
    case "THIRTEEN":
      tile.textContent = "13";
      break;
    case "JOKER":
      tile.textContent = "J";
      break;
  }
}

function dragEnter(event) {
  event.target.classList.add("dragover");
}

function dragLeave(event) {
  event.target.classList.remove("dragover");
}

function dragOver(event) {
  event.preventDefault();
}

function dragDrop(event) {
  event.preventDefault();
  if (event.target.classList.contains("tile")) {
    return;
  }
  if (event.target.classList.contains("droppable") && checkDrop(event.target)) {
    let parent = dragged.parentNode;
    parent.removeChild(dragged);
    parent.classList.add("droppable");
    parent.addEventListener("dragenter", dragEnter);
    parent.addEventListener("dragleave", dragLeave);
    parent.addEventListener("dragover", dragOver);
    parent.addEventListener("drop", dragDrop);
    event.target.appendChild(dragged);
    dragged.classList.remove("dragover");
  }
  dragLeave(event);
}

function checkDrop(target) {
  //td
  let tr = target.parentNode;

  //player board
  if (tr.classList.contains("player-tiles-row")) {
    if (dragged.parentNode.parentNode.classList.contains("player-tiles-row"))
      return true;
    //from table to board
    return false;
  }
  if (currentPlayer != userId) return false;
  //joker
  if (dragged.firstChild.data == "J") return true;
  //thirteen rows
  if (
    tr.classList.contains("red-thirteen-block-line") &&
    dragged.classList.contains("tile-red") &&
    target.firstChild.data == dragged.firstChild.data
  )
    return true;
  if (
    tr.classList.contains("black-thirteen-block-line") &&
    dragged.classList.contains("tile-black") &&
    target.firstChild.data == dragged.firstChild.data
  )
    return true;
  if (
    tr.classList.contains("orange-thirteen-block-line") &&
    dragged.classList.contains("tile-orange") &&
    target.firstChild.data == dragged.firstChild.data
  )
    return true;
  if (
    tr.classList.contains("blue-thirteen-block-line") &&
    dragged.classList.contains("tile-blue") &&
    target.firstChild.data == dragged.firstChild.data
  )
    return true;

  //four rows
  if (tr.classList.contains("four-block-lines")) {
    let trChildren = tr.children;
    if (
      dragged.classList.contains("tile-red") &&
      trChildren[0].classList.contains("dragover")
    )
      return true;
    if (
      dragged.classList.contains("tile-black") &&
      trChildren[1].classList.contains("dragover")
    )
      return true;
    if (
      dragged.classList.contains("tile-orange") &&
      trChildren[2].classList.contains("dragover")
    )
      return true;
    if (
      dragged.classList.contains("tile-blue") &&
      trChildren[3].classList.contains("dragover")
    )
      return true;
  }

  return false;
}

function saveBoard() {
  //user tiles
  let tiles = [];
  for (let rowCounter = 0; rowCounter < 2; rowCounter++) {
    let row = document
      .getElementById("player-tiles-table")
      .getElementsByTagName("tbody")[0].children[rowCounter];
    let tilesInRow = [];
    for (let column = 0; column < 21; column++) {
      let tile = row.cells[column];
      let tileToAdd;
      if (tile.children.length == 0) {
        tileToAdd = null;
      } else {
        let child = tile.children[0];
        tileToAdd = getTileFromChild(child);
      }
      tilesInRow.push(tileToAdd);
    }
    tiles.push(tilesInRow);
  }

  let state = {
    user: { gameId: gameId, userId: userId },
    table: null,
    tiles: tiles,
  };

  stompClient.send("/app/game/saveBoard", {}, JSON.stringify(state));
}

function endMove() {
  //four blocks
  let fourBlocks = [];
  for (let block = 0; block < 3; block++) {
    let blocks = document
      .getElementsByClassName("four-block-tables")
      [block].getElementsByTagName("tbody")[0];
    let tilesInBlock = [];
    for (let row = 0; row < 8; row++) {
      let rows = blocks.getElementsByClassName("four-block-lines")[row];
      let tilesInRow = [];
      for (let column = 0; column < 4; column++) {
        let tile = rows.cells[column];
        let tileToAdd;
        if (tile.children.length == 0) {
          tileToAdd = null;
        } else {
          let child = tile.children[0];
          tileToAdd = getTileFromChild(child);
        }
        tilesInRow.push(tileToAdd);
      }
      tilesInBlock.push(tilesInRow);
    }
    fourBlocks.push(tilesInBlock);
  }

  //thirteen blocks
  let thirteenBlocks = [];
  for (let rowCounter = 0; rowCounter < 8; rowCounter++) {
    let row = document
      .getElementsByClassName("thirteen-block-tables")[0]
      .getElementsByTagName("tbody")[0].children[rowCounter];
    let tilesInRow = [];
    for (let column = 0; column < 13; column++) {
      let tile = row.cells[column];
      let tileToAdd;
      if (tile.children.length == 0) {
        tileToAdd = null;
      } else {
        let child = tile.children[0];
        tileToAdd = getTileFromChild(child);
      }
      tilesInRow.push(tileToAdd);
    }
    thirteenBlocks.push(tilesInRow);
  }

  //user tiles
  let tiles = [];
  for (let rowCounter = 0; rowCounter < 2; rowCounter++) {
    let row = document
      .getElementById("player-tiles-table")
      .getElementsByTagName("tbody")[0].children[rowCounter];
    let tilesInRow = [];
    for (let column = 0; column < 21; column++) {
      let tile = row.cells[column];
      let tileToAdd;
      if (tile.children.length == 0) {
        tileToAdd = null;
      } else {
        let child = tile.children[0];
        tileToAdd = getTileFromChild(child);
      }
      tilesInRow.push(tileToAdd);
    }
    tiles.push(tilesInRow);
  }

  let state = {
    user: { gameId: gameId, userId: userId },
    table: { fourBlocks: fourBlocks, thirteenBlocks: thirteenBlocks },
    tiles: tiles,
  };

  stompClient.send("/app/game/updates", {}, JSON.stringify(state));
}

function getTileFromChild(child) {
  let childColor;
  if (child.classList.contains("tile-red")) childColor = "RED";
  if (child.classList.contains("tile-black")) childColor = "BLACK";
  if (child.classList.contains("tile-orange")) childColor = "ORANGE";
  if (child.classList.contains("tile-blue")) childColor = "BLUE";
  let childNumber;
  switch (child.innerText) {
    case "1":
      childNumber = "ONE";
      break;
    case "2":
      childNumber = "TWO";
      break;
    case "3":
      childNumber = "THREE";
      break;
    case "4":
      childNumber = "FOUR";
      break;
    case "5":
      childNumber = "FIVE";
      break;
    case "6":
      childNumber = "SIX";
      break;
    case "7":
      childNumber = "SEVEN";
      break;
    case "8":
      childNumber = "EIGHT";
      break;
    case "9":
      childNumber = "NINE";
      break;
    case "10":
      childNumber = "TEN";
      break;
    case "11":
      childNumber = "ELEVEN";
      break;
    case "12":
      childNumber = "TWELVE";
      break;
    case "13":
      childNumber = "THIRTEEN";
      break;
    case "J":
      childNumber = "JOKER";
      break;
  }
  return { color: childColor, number: childNumber };
}

function reset() {
  updateGame(update_);
}

function endGame() {
  end = true;
  let url = window.location.origin + "/win?gameId=" + gameId;
  window.location.replace(url);
}

function clickDrop(event) {
  event.target.classList.add("dragover");
  dragDrop(event);
}

window.onbeforeunload = (event) => {
  if (!end) event.preventDefault();
};

window.onunload = () => {
  if (!end)
    navigator.sendBeacon(
      "/game/leave",
      JSON.stringify({ userId: userId, gameId: gameId })
    );
};

window.onload = function () {
  connect();
  var elements = document.getElementsByClassName("droppable");

  for (var i = 0; i < elements.length; i++) {
    elements[i].addEventListener("dragenter", dragEnter);
    elements[i].addEventListener("dragleave", dragLeave);
    elements[i].addEventListener("dragover", dragOver);
    elements[i].addEventListener("drop", dragDrop);
    elements[i].addEventListener("click", clickDrop);
  }
};
