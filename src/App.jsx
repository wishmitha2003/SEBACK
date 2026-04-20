import React, { useEffect, useState } from "react";

function App() {
  const [progress, setProgress] = useState({
    averageScore: 0,
    quizzesAttempted: 0,
    attendance: 0
  });

  useEffect(() => {
    // Fake data (so it works without backend)
    setProgress({
      averageScore: 85,
      quizzesAttempted: 12,
      attendance: 90
    });
  }, []);

  return (
    <div style={{ padding: "20px", fontFamily: "Arial" }}>
      <h1 style={{ marginBottom: "20px" }}>Student Progress Dashboard</h1>

      <div style={{ display: "flex", gap: "20px" }}>
        
        <div style={{
          background: "#fff",
          padding: "20px",
          borderRadius: "10px",
          boxShadow: "0 0 10px rgba(0,0,0,0.1)",
          width: "200px"
        }}>
          <h3>Average Score</h3>
          <p style={{ fontSize: "20px" }}>{progress.averageScore}%</p>
        </div>

        <div style={{
          background: "#fff",
          padding: "20px",
          borderRadius: "10px",
          boxShadow: "0 0 10px rgba(0,0,0,0.1)",
          width: "200px"
        }}>
          <h3>Quizzes Attempted</h3>
          <p style={{ fontSize: "20px" }}>{progress.quizzesAttempted}</p>
        </div>

        <div style={{
          background: "#fff",
          padding: "20px",
          borderRadius: "10px",
          boxShadow: "0 0 10px rgba(0,0,0,0.1)",
          width: "200px"
        }}>
          <h3>Attendance</h3>
          <p style={{ fontSize: "20px" }}>{progress.attendance}%</p>
        </div>

      </div>
    </div>
  );
}

export default App;