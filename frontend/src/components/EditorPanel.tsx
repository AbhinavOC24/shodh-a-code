"use client";
import { useEffect, useState } from "react";
import AceEditor from "react-ace";

import "ace-builds/src-noconflict/mode-java";
import "ace-builds/src-noconflict/mode-python";
import "ace-builds/src-noconflict/mode-c_cpp";
import "ace-builds/src-noconflict/theme-dracula";

const LANGUAGES = [
  { label: "Java", value: "java", aceMode: "java" },
  { label: "Python", value: "python", aceMode: "python" },
  { label: "C++", value: "cpp", aceMode: "c_cpp" },
];

export default function EditorPanel({
  onSubmit,
  language,
  setLanguage,
  problemId,
}: {
  onSubmit: (code: string) => void;
  language: string;
  setLanguage: (val: string) => void;
  problemId: number | string;
}) {
  const userId = localStorage.getItem("userId");
  const codeKey = `code_${userId}_${problemId}`;
  const langKey = `lang_${userId}_${problemId}`;

  const [code, setCode] = useState("// Write your code here");

  useEffect(() => {
    const savedCode = localStorage.getItem(codeKey);
    const savedLang = localStorage.getItem(langKey);

    if (savedCode) setCode(savedCode);
    else setCode("// Write your code here");

    if (savedLang) setLanguage(savedLang);
  }, [problemId]);

  const handleCodeChange = (newCode: string) => {
    setCode(newCode);
    localStorage.setItem(codeKey, newCode);
  };

  const handleLanguageChange = (newLang: string) => {
    setLanguage(newLang);
    localStorage.setItem(langKey, newLang);
  };

  const mode = LANGUAGES.find((l) => l.value === language)?.aceMode || "java";

  return (
    <div className="flex flex-col h-full bg-[#1e1e1e]">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-gray-700 px-4 py-2 text-sm">
        <div className="flex items-center gap-4">
          <span className="text-blue-400 font-medium">Code Editor</span>

          <select
            value={language}
            onChange={(e) => handleLanguageChange(e.target.value)}
            className="ml-4 bg-transparent text-gray-300 border border-gray-600 rounded px-2 py-0.5 text-xs focus:outline-none"
          >
            {LANGUAGES.map((lang) => (
              <option
                key={lang.value}
                value={lang.value}
                className="bg-[#1e1e1e] text-white"
              >
                {lang.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Code Editor */}
      <div className="flex-1 overflow-hidden">
        <AceEditor
          mode={mode}
          theme="dracula"
          value={code}
          onChange={handleCodeChange}
          width="100%"
          height="100%"
          fontSize={14}
          setOptions={{
            enableBasicAutocompletion: true,
            enableLiveAutocompletion: true,
            showLineNumbers: true,
            tabSize: 2,
          }}
        />
      </div>

      {/* Footer Buttons */}
      <div className="flex justify-end gap-3 p-3 border-t border-gray-700">
        <button className="bg-gray-700 text-white px-4 py-1.5 rounded text-sm">
          Run
        </button>
        <button
          onClick={() => onSubmit(code)}
          className="bg-green-600 text-white px-4 py-1.5 rounded text-sm"
        >
          Submit
        </button>
      </div>
    </div>
  );
}
