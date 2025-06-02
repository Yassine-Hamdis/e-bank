import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AgentService } from '../../services/agent.service';
import { User, Transaction, VerifyTransactionRequest } from '../../models/auth.model';
import { AgentSidebarComponent } from '../agent-sidebar/agent-sidebar.component';

@Component({
  selector: 'app-agent-transaction-management',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, AgentSidebarComponent],
  templateUrl: './agent-transaction-management.component.html',
  styleUrls: ['./agent-transaction-management.component.css']
})
export class AgentTransactionManagementComponent implements OnInit {
  user: User | null = null;

  // Transaction properties
  transactions: Transaction[] = [];
  pendingTransactions: Transaction[] = [];
  isLoadingTransactions = true;
  transactionsError: string | null = null;
  selectedTransaction: Transaction | null = null;
  showVerificationModal = false;
  verificationStatus: 'VERIFIED' | 'REJECTED' = 'VERIFIED';
  verificationNotes = '';
  isVerifying = false;
  verificationSuccess = '';
  verificationError = '';

  constructor(
    private authService: AuthService,
    private agentService: AgentService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    // Check if user is agent
    if (!this.authService.isAgent()) {
      this.router.navigate(['/dashboard']);
      return;
    }

    this.user = this.authService.getCurrentUser();
    this.loadTransactions();
  }

  private loadTransactions(): void {
    this.isLoadingTransactions = true;
    this.transactionsError = null;

    // Load all transactions
    this.agentService.getAllTransactions().subscribe({
      next: (transactions) => {
        this.transactions = transactions;
        this.loadPendingTransactions();
      },
      error: (error) => {
        console.error('Error loading transactions:', error);
        this.transactionsError = 'Failed to load transactions. Please try again.';
        this.isLoadingTransactions = false;
      }
    });
  }

  private loadPendingTransactions(): void {
    this.agentService.getPendingTransactions().subscribe({
      next: (pendingTransactions) => {
        this.pendingTransactions = pendingTransactions;
        this.isLoadingTransactions = false;
      },
      error: (error) => {
        console.error('Error loading pending transactions:', error);
        this.transactionsError = 'Failed to load pending transactions. Please try again.';
        this.isLoadingTransactions = false;
      }
    });
  }

  refreshTransactions(): void {
    this.loadTransactions();
  }

  // Transaction verification methods
  openVerificationModal(transaction: Transaction): void {
    this.selectedTransaction = transaction;
    this.verificationStatus = 'VERIFIED';
    this.verificationNotes = '';
    this.verificationSuccess = '';
    this.verificationError = '';
    this.showVerificationModal = true;
  }

  closeVerificationModal(): void {
    this.showVerificationModal = false;
    this.selectedTransaction = null;
    this.verificationNotes = '';
    this.isVerifying = false;
    this.verificationSuccess = '';
    this.verificationError = '';
  }

  verifyTransaction(): void {
    if (!this.selectedTransaction || !this.verificationNotes.trim()) {
      return;
    }

    this.isVerifying = true;
    this.verificationSuccess = '';
    this.verificationError = '';

    const verificationData: VerifyTransactionRequest = {
      status: this.verificationStatus,
      description: this.verificationNotes.trim()
    };

    this.agentService.verifyTransaction(this.selectedTransaction.transactionId, verificationData).subscribe({
      next: (response) => {
        console.log('Transaction verified successfully:', response);
        // Display API response message if available, otherwise use default
        this.verificationSuccess = response.message || `Transaction ${this.verificationStatus.toLowerCase()} successfully!`;
        this.isVerifying = false;

        // Auto-close modal after 2 seconds and refresh transactions
        setTimeout(() => {
          this.closeVerificationModal();
          this.refreshTransactions();
        }, 2000);
      },
      error: (error) => {
        console.error('Error verifying transaction:', error);
        // Display API error message if available, otherwise use default
        this.verificationError = error.error?.message || 'Failed to verify transaction. Please try again.';
        this.isVerifying = false;
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  // Transaction statistics
  get totalTransactions(): number {
    return this.transactions.length;
  }

  get pendingTransactionsCount(): number {
    return this.pendingTransactions.length;
  }

  get verifiedTransactions(): number {
    return this.transactions.filter(t => 
      t.status === 'Verified' || t.status === 'VERIFIED'
    ).length;
  }

  get rejectedTransactions(): number {
    return this.transactions.filter(t => 
      t.status === 'Rejected' || t.status === 'REJECTED'
    ).length;
  }

  // Utility methods
  getStatusClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'pending':
        return 'bg-yellow-100 text-yellow-800';
      case 'verified':
        return 'bg-green-100 text-green-800';
      case 'rejected':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatAmount(amount: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  }
}
