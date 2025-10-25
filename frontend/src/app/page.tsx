"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";

export default function JoinPage() {
  const [contestId, setContestId] = useState("");
  const [username, setUsername] = useState("");
  const router = useRouter();

  const handleJoin = () => {
    if (!contestId || !username) return;
    localStorage.setItem("username", username);
    router.push(`/contest/${contestId}`);
  };

  return (
    <main className="flex flex-col items-center justify-center h-screen gap-4">
      <h1 className="text-3xl font-bold">Join Contest</h1>
      <input
        className="border p-2 rounded w-64"
        placeholder="Contest ID"
        value={contestId}
        onChange={(e) => setContestId(e.target.value)}
      />
      <input
        className="border p-2 rounded w-64"
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <button
        onClick={handleJoin}
        className="bg-blue-600 text-white px-4 py-2 rounded"
      >
        Join
      </button>
    </main>
  );
}
