import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../core/services/admin.service';
import { ReportResponse } from '../../../core/models/report';

@Component({
  selector: 'app-admin-reports',
  imports: [CommonModule],
  templateUrl: './admin-reports.html',
  styleUrl: './admin-reports.scss'
})
export class AdminReports implements OnInit {
  reports: ReportResponse[] = [];
  selectedReport: ReportResponse | null = null;
  private adminService = inject(AdminService);

  ngOnInit() {
    this.loadReports();
  }

  loadReports() {
    this.adminService.getAllReports().subscribe({
      next: (reports) => {
        console.log(reports);

        this.reports = reports;
      },
      error: (error) => {
        console.error('Error loading reports:', error);
      }
    });
  }

  onViewClick(report: ReportResponse) {
    this.selectedReport = report;
    console.log("on View Click", this.selectedReport);
  }

  closeModal() {
    this.selectedReport = null;
  }


  onDeletePostClick(reportId: string) {
    if (confirm('Are you sure you want to delete this post?')) {
      this.adminService.deletePost(reportId).subscribe({
        next: () => {
          // Remove the report from the list after deleting the post
          this.reports = this.reports.filter(r => r.reportId !== reportId);
          alert('Post deleted successfully');
        },
        error: (err: any) => {
          console.error(err);
          alert('Failed to delete post');
        }
      });
    }
  }

onBanUserClick(userId: string, reportId: string) {
  if (confirm('Are you sure you want to ban this user?')) {
    this.adminService.banUserFromReport(reportId, userId).subscribe({
      next: (updatedReport) => {
        // Update the local list
        this.reports = this.reports.map(r =>
          r.reportId === updatedReport.reportId ? updatedReport : r
        );
        alert('User banned successfully');
      },
      error: (err) => {
        console.error(err);
        alert('Failed to ban user');
      }
    });
  }
}

  onDismissClick(reportId: string) {
    this.adminService.putDismiss(reportId).subscribe({
      next: (updatedReport) => {
        // Update the local list
        this.reports = this.reports.map(r =>
          r.reportId === updatedReport.reportId ? updatedReport : r
        );
      },
      error: (err) => {
        console.error(err);
        alert('Failed to dismiss report');
      }
    });
  }
}
