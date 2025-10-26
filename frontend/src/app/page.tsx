"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";

export default function JoinPage() {
  const [contestId, setContestId] = useState("");
  const [username, setUsername] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const router = useRouter();

  const handleJoin = async () => {
    if (!contestId || !username) {
      setError("Please enter both Contest ID and Username.");
      return;
    }

    setLoading(true);
    setError("");

    try {
      const res = await axios.post(
        `http://localhost:8080/api/contests/${contestId}/join`,
        { username }
      );

      const data = res.data;

      localStorage.setItem("username", data.user);
      localStorage.setItem("userId", data.userId);
      localStorage.setItem("contestId", contestId);

      router.push(`/contest/${contestId}`);
    } catch (err: any) {
      console.error("Join failed:", err);
      setError(err.response?.data?.message || "Failed to join contest.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="flex flex-col items-center justify-center h-screen bg-[#0e0e0e] text-gray-100">
      {/* Main heading */}
      <h1 className="text-4xl font-bold mb-8 text-blue-500">Shodh-a-Code</h1>

      {/* Card box */}
      <div className="bg-[#1a1a1a] border border-gray-700 rounded-2xl p-8 shadow-xl flex flex-col items-center w-[320px] gap-4">
        <h2 className="text-2xl font-semibold mb-2 text-gray-200">
          Join Contest
        </h2>

        <input
          className="border border-gray-600 bg-[#0f0f0f] text-gray-200 p-2 rounded w-full focus:outline-none focus:border-blue-500"
          placeholder="Contest ID (Only 1 is available)"
          value={contestId}
          onChange={(e) => setContestId(e.target.value)}
        />

        <input
          className="border border-gray-600 bg-[#0f0f0f] text-gray-200 p-2 rounded w-full focus:outline-none focus:border-blue-500"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <button
          onClick={handleJoin}
          disabled={loading}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded w-full font-medium disabled:opacity-60 transition-colors"
        >
          {loading ? "Joining..." : "Join"}
        </button>

        {error && (
          <p className="text-red-500 text-sm mt-2 text-center">{error}</p>
        )}
      </div>
    </main>
  );
}
