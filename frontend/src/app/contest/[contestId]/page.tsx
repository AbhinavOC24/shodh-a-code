"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { getContest, postSubmission, getSubmission } from "@/lib/api";
import ProblemPanel from "@/components/ProblemPanel";
import EditorPanel from "@/components/EditorPanel";
import OutputBar from "@/components/OutputBar";
import LeaderboardTable from "@/components/LeaderboardTable";
import axios from "axios";
export default function ContestPage() {
  const { contestId } = useParams();
  const [contest, setContest] = useState<any>(null);
  const [problem, setProblem] = useState<any>(null);
  const [status, setStatus] = useState("");
  const [language, setLanguage] = useState("java");

  useEffect(() => {
    if (!contestId) return;
    (async () => {
      const data = await getContest(contestId as string);
      setContest(data);
      setProblem(data.problems?.[0]);
    })();
  }, [contestId]);

  const handleSubmit = async (code: string) => {
    if (!problem || !contestId) return;
    setStatus("RUNNING");

    const userId = localStorage.getItem("userId");

    const payload = {
      user: { id: Number(userId) },
      contest: { id: Number(contestId) },
      problem: { id: problem.id },
      language,
      sourceCode: code,
    };

    try {
      const sub = await postSubmission(payload);
      pollStatus(String(sub.id));
    } catch (err) {
      console.error(err);
      setStatus("Submission failed.");
    }
  };

  const pollStatus = (id: string) => {
    const interval = setInterval(async () => {
      try {
        const res = await getSubmission(id);
        setStatus(
          res.status + (res.verdictMessage ? ` - ${res.verdictMessage}` : "")
        );

        if (
          ["ACCEPTED", "WRONG_ANSWER", "RTE", "CE", "TLE"].includes(res.status)
        ) {
          clearInterval(interval);

          if (res.status === "ACCEPTED") {
            try {
              const userId = localStorage.getItem("userId");
              if (userId && problem?.id) {
                await axios.post(
                  `http://localhost:8080/api/contests/${contestId}/update-score`,
                  {
                    userId: Number(userId),
                    problemId: problem.id,
                  }
                );
              }
            } catch (err) {
              console.error("Failed to update score:", err);
            }
          }
        }
      } catch (e) {
        console.error(e);
        clearInterval(interval);
        setStatus("Error fetching submission status.");
      }
    }, 15000);
  };

  if (!contest || !problem)
    return (
      <div className="p-6 text-gray-300 font-mono text-sm">
        Loading contest...
      </div>
    );

  return (
    <main className="flex h-screen bg-[#0d0d0d] text-white">
      {/* LEFT: Problem (top) + Leaderboard (bottom) */}
      <div className="w-1/2 border-r border-zinc-800 flex flex-col">
        <div className="flex-1 overflow-auto">
          <ProblemPanel
            problem={problem}
            contest={contest}
            onSelectProblem={(p) => setProblem(p)}
          />
        </div>
        <div className="h-[35%] border-t border-zinc-800 overflow-auto">
          <LeaderboardTable contestId={contestId as string} />
        </div>
      </div>

      {/* RIGHT: Code editor */}
      <div className="w-1/2 flex flex-col">
        <EditorPanel
          onSubmit={handleSubmit}
          language={language}
          setLanguage={setLanguage}
          problemId={problem?.id}
        />

        <OutputBar status={status} />
      </div>
    </main>
  );
}
