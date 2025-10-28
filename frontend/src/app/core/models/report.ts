export interface ReportResponse {
  reportId: string;
  reporterId: string;
  reporterUsername: string;
  reportedUserId: string;
  reportedUserUsername: string;
  reason: string;
  timestamp: string;
  status: ReportStatus;
}

export enum ReportStatus {
  PENDING = 'PENDING',
  RESOLVED = 'RESOLVED'
}
