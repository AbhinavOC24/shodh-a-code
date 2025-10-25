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
    <main className="flex flex-col items-center justify-center h-screen bg-[#0e0e0e] text-gray-100 gap-4">
      <h1 className="text-3xl font-bold">Join Contest</h1>

      <input
        className="border border-gray-600 bg-[#1a1a1a] text-gray-200 p-2 rounded w-64"
        placeholder="Contest ID"
        value={contestId}
        onChange={(e) => setContestId(e.target.value)}
      />

      <input
        className="border border-gray-600 bg-[#1a1a1a] text-gray-200 p-2 rounded w-64"
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />

      <button
        onClick={handleJoin}
        disabled={loading}
        className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded disabled:opacity-60"
      >
        {loading ? "Joining..." : "Join"}
      </button>

      {error && <p className="text-red-500 text-sm mt-2">{error}</p>}
    </main>
  );
}
