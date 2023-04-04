import React, { Fragment }  from 'react';
import { useState } from 'react';

function Square({value, onSquareClick}) {
  return (
    <button className='square' onClick={onSquareClick}>
      {value}
      </button>
  );
}

// helper function to check adjacency easier
function parseToCoordinates(i) {
  const coordinates = [
    [0, 0], [0, 1], [0, 2],
    [1, 0], [1, 1], [1, 2],
    [2, 0], [2, 1], [2, 2],
  ];

  return coordinates[i];
}

// helper function to determine if two cells are adjacent based on their indices
function isAdjacent(i, j) {
  // if they're the same point, return true
  if(i === j)
    return true;

  // compares array by elements
  const cmp = (arr1, arr2) => { return arr1.toString() === arr2.toString(); }
  const iCoordinates = parseToCoordinates(i);
  const jCoordinates = parseToCoordinates(j);
  const moves = [
    // up    down    left     right
    [-1, 0], [1, 0], [0, -1], [0, 1],
    // dlup   drup    dldown    drdown          dl = diagonal left, dr = diagonal right
    [-1, -1], [-1, 1], [1, -1], [1, 1]
  ]

  // if we can reach j from i in adjacent moves (try all possiblem moves)
  for(let i = 0; i < moves.length; i++)
    if(cmp(iCoordinates.map((num, k) => { return num + moves[i][k];}), jCoordinates))
      return true;

  // i and j are not adjacent
  return false;
}

// helper function to determine the winner (or lack thereof)
function calculateWinner(squares) {
  const lines  = [
    [0, 1, 2],
    [3, 4, 5],
    [6, 7, 8],
    [0, 3, 6],
    [1, 4, 7],
    [2, 5, 8],
    [0, 4, 8],
    [2, 4, 6],
  ];

  for(let i = 0; i < lines.length; i++) {
    const [a, b, c] = lines[i];
    if (squares[a] && squares[a] === squares[b] && squares[a] === squares[c])
      return squares[a];
  }

  return null;
}

export default function Game() {
  // turns
  const [xIsNext, setXIsNext] = useState(true);
  // board
  const [squares, setSquares] = useState(Array(9).fill(null));
  // there are n pieces on the board
  const [iterations, setIterations] = useState(0);
  // previous i
  const [prevI, setPrevI] = useState(-1);

  // general function to handle clicks
  const handleClick = (i) => {
    if(calculateWinner(squares)) {
      console.log('a')
      return;
    }
    
    const nextSquares = squares.slice();

    // if we're moving pieces (2 click process)
    if(iterations > 5)
      // second click
      if(!squares[prevI]) {
        // cell is occupied or isn't adjacent
        if(squares[i] || !isAdjacent(i, prevI)) {
          console.log('cell is occupied or is not adjacent')
          return;
        }
        
        // if the center is not empty
        if(squares[4] === (xIsNext ? 'X' : 'O')) {
          nextSquares[i] = (xIsNext ? 'X' : 'O');
          // if you don't move the center and it's not a winning move, don't let them make that move
          if(!calculateWinner(nextSquares) && i !== prevI) {
            nextSquares[i] = null;
            console.log('the only option is to undo the move')
            return;
          }
        }

        // undo move (don't change turns)
        if(i === prevI) {
          nextSquares[i] = (xIsNext ? 'X' : 'O');
          setSquares(nextSquares);
          console.log('don\'t change turns')
          return;
        }
      }

      // first click
      else {
        if(!squares[i]){
          console.log('cell is empty')
          return;
        }
        
        // if you're clicking on your opponent's piece
        if(squares[i] !== (xIsNext ? 'X' : 'O')){
          console.log('invalid option (not your piece)')
          return;
        }

        nextSquares[i] = null;
        setSquares(nextSquares);
        setIterations(iterations + 1);
        setPrevI(i);
      }
    // adding pieces
    // if the cell is nonempty
    if(squares[i]){
      console.log('cell is nonempty')
      return;
    }

    // add a piece
    nextSquares[i] = (xIsNext ? 'X' : 'O');
    setSquares(nextSquares);
    setIterations(iterations + 1);
    setPrevI(i);
    setXIsNext(!xIsNext);
  }
  
  // check if there's a winner
  const winner = calculateWinner(squares);

  let status, howTo;

  // set status
  winner ? status = 'Winner: ' + winner : status = 'Next player: ' + ((xIsNext ? 'X' : 'O'));

  // set howTo if in chorus mode
  iterations > 5 ? howTo = <div><h1>You are now in chorus mode:</h1><ul>
    <li>Move one of your existing pieces to an adjacent empty square (the move can be up, down, left, right, or diagonal)</li>
    <li>If it is your turn to move and you have three pieces on the board and one of your pieces is in the center square, your move must either win or vacate the center square</li>
</ul></div> : howTo = '';

const board = [
  [<Square value={squares[0]} onSquareClick={() => handleClick(0)}/>, <Square value={squares[1]} onSquareClick={() => handleClick(1)}/>, <Square value={squares[2]} onSquareClick={() => handleClick(2)}/>], 
  [<Square value={squares[3]} onSquareClick={() => handleClick(3)}/>, <Square value={squares[4]} onSquareClick={() => handleClick(4)}/>, <Square value={squares[5]} onSquareClick={() => handleClick(5)}/>],
  [<Square value={squares[6]} onSquareClick={() => handleClick(6)}/>, <Square value={squares[7]} onSquareClick={() => handleClick(7)}/>, <Square value={squares[8]} onSquareClick={() => handleClick(8)}/>]
];

  return (
  <Fragment>
    <div className='status'>{status}</div>
    <div className='board-row'>
    {board[0][0]}
    {board[0][1]}
    {board[0][2]}
    </div>
    <div className='board-row'>
    {board[1][0]}
    {board[1][1]}
    {board[1][2]}
    </div>
    <div className='board-row'>
    {board[2][0]}
    {board[2][1]}
    {board[2][2]}
    </div>
    <div className='h2'><br/>{howTo}</div>
  </Fragment>
  );
}