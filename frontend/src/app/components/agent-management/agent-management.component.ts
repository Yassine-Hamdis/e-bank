import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AdminService } from '../../services/admin.service';
import { Agent, CreateAgentRequest, User } from '../../models/auth.model';
import { AdminSidebarComponent } from '../admin-sidebar/admin-sidebar.component';

@Component({
  selector: 'app-agent-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, AdminSidebarComponent],
  templateUrl: './agent-management.component.html',
  styleUrls: ['./agent-management.component.css']
})
export class AgentManagementComponent implements OnInit {
  user: User | null = null;
  agents: Agent[] = [];
  isLoading = true;
  error: string | null = null;
  
  // Modal states
  showCreateModal = false;
  showDeleteModal = false;
  selectedAgent: Agent | null = null;
  
  // Form
  createAgentForm: FormGroup;
  isCreating = false;
  createError: string | null = null;
  
  // Delete state
  isDeleting = false;
  deleteError: string | null = null;

  constructor(
    private authService: AuthService,
    private adminService: AdminService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.createAgentForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required, Validators.pattern(/^\+?[1-9]\d{1,14}$/)]],
      employeeId: ['', [Validators.required]],
      branch: ['', [Validators.required]],
      position: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }

    if (!this.authService.isAdmin()) {
      this.router.navigate(['/dashboard']);
      return;
    }
    
    this.user = this.authService.getCurrentUser();
    this.loadAgents();
  }

  loadAgents(): void {
    this.isLoading = true;
    this.error = null;

    this.adminService.getAllAgents().subscribe({
      next: (agents) => {
        this.agents = agents;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading agents:', error);
        this.error = 'Failed to load agents. Please try again.';
        this.isLoading = false;
      }
    });
  }

  openCreateModal(): void {
    this.showCreateModal = true;
    this.createAgentForm.reset();
    this.createError = null;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
    this.createAgentForm.reset();
    this.createError = null;
  }

  createAgent(): void {
    if (this.createAgentForm.valid) {
      this.isCreating = true;
      this.createError = null;

      const agentData: CreateAgentRequest = this.createAgentForm.value;

      this.adminService.createAgent(agentData).subscribe({
        next: (newAgent) => {
          this.agents.push(newAgent);
          this.isCreating = false;
          this.closeCreateModal();
        },
        error: (error) => {
          console.error('Error creating agent:', error);
          this.createError = error.error?.message || 'Failed to create agent. Please try again.';
          this.isCreating = false;
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  toggleAgentStatus(agent: Agent): void {
    const newStatus = agent.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    
    this.adminService.updateAgentStatus(agent.id, newStatus).subscribe({
      next: (updatedAgent) => {
        const index = this.agents.findIndex(a => a.id === agent.id);
        if (index !== -1) {
          this.agents[index] = updatedAgent;
        }
      },
      error: (error) => {
        console.error('Error updating agent status:', error);
        this.error = 'Failed to update agent status. Please try again.';
      }
    });
  }

  openDeleteModal(agent: Agent): void {
    this.selectedAgent = agent;
    this.showDeleteModal = true;
    this.deleteError = null;
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.selectedAgent = null;
    this.deleteError = null;
  }

  confirmDelete(): void {
    if (this.selectedAgent) {
      this.isDeleting = true;
      this.deleteError = null;

      this.adminService.deleteAgent(this.selectedAgent.id).subscribe({
        next: () => {
          this.agents = this.agents.filter(a => a.id !== this.selectedAgent!.id);
          this.isDeleting = false;
          this.closeDeleteModal();
        },
        error: (error) => {
          console.error('Error deleting agent:', error);
          this.deleteError = error.error?.message || 'Failed to delete agent. Please try again.';
          this.isDeleting = false;
        }
      });
    }
  }

  private markFormGroupTouched(): void {
    Object.keys(this.createAgentForm.controls).forEach(key => {
      this.createAgentForm.get(key)?.markAsTouched();
    });
  }

  getFieldError(fieldName: string): string {
    const field = this.createAgentForm.get(fieldName);
    if (field?.errors && field.touched) {
      if (field.errors['required']) return `${fieldName} is required`;
      if (field.errors['email']) return 'Please enter a valid email';
      if (field.errors['minlength']) return `${fieldName} must be at least ${field.errors['minlength'].requiredLength} characters`;
      if (field.errors['pattern']) return 'Please enter a valid phone number';
    }
    return '';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  navigateToAdmin(): void {
    this.router.navigate(['/admin']);
  }
}
