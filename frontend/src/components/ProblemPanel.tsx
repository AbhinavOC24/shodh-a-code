"use client";
import { useState } from "react";

export default function ProblemPanel({
  problem,
  contest,
}: {
  problem: any;
  contest?: any;
}) {
  const [selected, setSelected] = useState(problem);

  const problems = contest?.problems || [problem];

  return (
    <div className="flex flex-col h-full bg-[#0e0e0e] text-gray-200">
      {/* Header with problem tabs */}
      <div className="flex border-b border-zinc-800 overflow-x-auto">
        {problems.map((p: any) => (
          <button
            key={p.id}
            onClick={() => setSelected(p)}
            className={`px-4 py-2 text-sm whitespace-nowrap ${
              selected?.id === p.id
                ? "border-b-2 border-blue-500 text-blue-400"
                : "text-gray-400"
            }`}
          >
            {p.title}
          </button>
        ))}
      </div>

      {/* Problem details */}
      <div className="flex-1 overflow-y-auto p-6">
        <h2 className="text-2xl font-semibold mb-3">{selected?.title}</h2>
        <p className="text-sm leading-relaxed text-gray-300 whitespace-pre-wrap">
          {selected?.statement}
        </p>

        {/* Test Cases Section */}
        <div className="mt-6">
          <h3 className="font-semibold text-gray-100 mb-2">Test Cases</h3>
          {selected?.testCases?.map((tc: any, i: number) => (
            <div
              key={tc.id}
              className="border border-zinc-700 rounded-lg p-3 mb-2 bg-[#1a1a1a]"
            >
              <div className="text-xs text-gray-400 mb-1">Case {i + 1}</div>
              <div className="text-sm">
                <div>
                  <span className="font-medium text-gray-300">Input:</span>{" "}
                  <span className="text-gray-400">{tc.inputData}</span>
                </div>
                <div>
                  <span className="font-medium text-gray-300">Expected:</span>{" "}
                  <span className="text-gray-400">{tc.expectedOutput}</span>
                </div>
              </div>
            </div>
          ))}
          {!selected?.testCases?.length && (
            <p className="text-xs text-gray-500">
              No sample test cases available.
            </p>
          )}
        </div>
      </div>
    </div>
  );
}
