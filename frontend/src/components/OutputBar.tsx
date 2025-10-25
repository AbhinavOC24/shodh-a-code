export default function OutputBar({ status }: { status: string }) {
  return (
    <div className="bg-zinc-800 text-gray-200 px-4 py-2 text-sm font-mono border-t border-zinc-700">
      {status ? `Status: ${status}` : "Ready to run your code."}
    </div>
  );
}
