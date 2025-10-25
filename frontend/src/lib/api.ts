import axios from "axios";
const API = "http://localhost:8080/api";

export const getContest = (id: string) =>
  axios.get(`${API}/contests/${id}`).then((r) => r.data);
export const postSubmission = (data: any) =>
  axios.post(`${API}/submissions`, data).then((r) => r.data);
export const getSubmission = (id: string) =>
  axios.get(`${API}/submissions/${id}`).then((r) => r.data);
export const getLeaderboard = (contestId: string) =>
  axios.get(`${API}/contests/${contestId}/leaderboard`).then((r) => r.data);
