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

  onDismissClick(reportId: string) {
    this.adminService.putDismiss(reportId).subscribe({
      next: (updatedReport) => {
        // Update the local list
        this.reports = this.reports.map(r =>
          r.reportId === updatedReport.reportId ? updatedReport : r
        );

        alert('Report dismissed successfully');
      },
      error: (err) => {
        console.error(err);
        alert('Failed to dismiss report');
      }
    });
  }
}
