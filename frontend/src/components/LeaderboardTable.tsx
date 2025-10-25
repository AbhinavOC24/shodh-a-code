"use client";
import { useEffect, useState } from "react";
import { getLeaderboard } from "@/lib/api";

export default function LeaderboardTable({ contestId }: { contestId: string }) {
  const [leaderboard, setLeaderboard] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!contestId) return;

    const fetchLeaderboard = async () => {
      try {
        const data = await getLeaderboard(contestId);
        setLeaderboard(data);
      } catch (err) {
        console.error("Leaderboard fetch failed:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchLeaderboard();
    const interval = setInterval(fetchLeaderboard, 2000);
    return () => clearInterval(interval);
  }, [contestId]);

  return (
    <div className="p-4 h-full bg-[#141414] border-t border-gray-800 overflow-y-auto">
      {/* Heading */}
      <div className="flex items-center justify-between mb-3">
        <h3 className="text-base font-semibold text-gray-100">
          Live Leaderboard <span className="text-green-500">●</span>
        </h3>
        <span className="text-xs text-gray-500">auto-updates every 15s</span>
      </div>

      {/* Loading / Empty */}
      {loading ? (
        <div className="text-gray-400 text-sm py-6">Loading leaderboard...</div>
      ) : leaderboard.length === 0 ? (
        <div className="text-gray-500 text-sm py-6">
          No participants yet. Be the first one to join!
        </div>
      ) : (
        <table className="w-full text-sm border-collapse">
          <thead className="border-b border-gray-700 text-gray-400">
            <tr>
              <th className="py-1 text-left w-1/5">Rank</th>
              <th className="py-1 text-left w-3/5">Username</th>
              <th className="py-1 text-right w-1/5">Score</th>
            </tr>
          </thead>
          <tbody>
            {leaderboard.map((entry, i) => (
              <tr
                key={i}
                className={`border-b border-gray-800 hover:bg-[#1e1e1e] transition-colors ${
                  i === 0 ? "text-yellow-400" : "text-gray-200"
                }`}
              >
                <td className="py-2">{i + 1}</td>
                <td className="py-2 font-medium">{entry.username}</td>
                <td className="py-2 text-right text-green-400">
                  {entry.score}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
