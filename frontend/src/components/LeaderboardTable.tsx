"use client";
import { useEffect, useState } from "react";
import { getLeaderboard } from "@/lib/api";

export default function LeaderboardTable({ contestId }: { contestId: string }) {
  const [leaderboard, setLeaderboard] = useState<any[]>([]);

  // 🔁 Poll leaderboard every 20 seconds
  useEffect(() => {
    if (!contestId) return;
    const fetchLeaderboard = async () => {
      try {
        const data = await getLeaderboard(contestId);
        setLeaderboard(data);
      } catch (err) {
        console.error("Leaderboard fetch failed:", err);
      }
    };

    fetchLeaderboard(); // initial load
    const interval = setInterval(fetchLeaderboard, 20000);
    return () => clearInterval(interval);
  }, [contestId]);

  if (!leaderboard.length)
    return (
      <div className="p-4 text-gray-400 text-sm">
        No submissions yet. Be the first one!
      </div>
    );

  return (
    <div className="p-4">
      <h3 className="text-base font-semibold mb-2 text-gray-100">
        Live Leaderboard
      </h3>
      <table className="w-full text-sm border-collapse">
        <thead className="border-b border-gray-700 text-gray-400">
          <tr>
            <th className="py-1 text-left">Rank</th>
            <th className="py-1 text-left">Username</th>
            <th className="py-1 text-right">Score</th>
          </tr>
        </thead>
        <tbody>
          {leaderboard.map((entry, i) => (
            <tr key={i} className="border-b border-gray-800">
              <td className="py-1 text-gray-400">{i + 1}</td>
              <td className="py-1">{entry.username}</td>
              <td className="py-1 text-green-400 text-right">{entry.score}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
