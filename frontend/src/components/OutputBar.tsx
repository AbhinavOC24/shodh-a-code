export default function OutputBar({ status }: { status: string }) {
  const normalizedStatus = status.replace(/Test\s(\d+)/, (match, id) => {
    const offset = Number(id) > 2 ? Number(id) - 2 : id;
    return `Test ${offset}`;
  });
  return (
    <div className="bg-zinc-800 text-gray-200 px-4 py-2 text-sm font-mono border-t border-zinc-700">
      {status ? `Status: ${normalizedStatus}` : "Ready to run your code."}
    </div>
  );
}
