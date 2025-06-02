import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';
import { AdminSystemSettingsComponent } from './components/admin-system-settings/admin-system-settings.component';
import { AgentManagementComponent } from './components/agent-management/agent-management.component';
import { CurrencyManagementComponent } from './components/currency-management/currency-management.component';
import { AgentDashboardComponent } from './components/agent-dashboard/agent-dashboard.component';
import { AgentTransactionManagementComponent } from './components/agent-transaction-management/agent-transaction-management.component';
import { ClientManagementComponent } from './components/client-management/client-management.component';
import { ClientDashboardComponent } from './components/client-dashboard/client-dashboard.component';
import { ClientTransferComponent } from './components/client-transfer/client-transfer.component';
import { MobileRechargeComponent } from './components/mobile-recharge/mobile-recharge.component';
import { ClientNotificationsComponent } from './components/client-notifications/client-notifications.component';
import { CryptoWalletPageComponent } from './components/crypto-wallet-page/crypto-wallet-page.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { AuthGuard } from './guards/auth.guard';
import { AgentGuard } from './guards/agent.guard';
import { ClientGuard } from './guards/client.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'admin', component: AdminDashboardComponent, canActivate: [AuthGuard] },
  { path: 'admin/agents', component: AgentManagementComponent, canActivate: [AuthGuard] },
  { path: 'admin/currencies', component: CurrencyManagementComponent, canActivate: [AuthGuard] },
  { path: 'admin/system-settings', component: AdminSystemSettingsComponent, canActivate: [AuthGuard] },
  { path: 'agent', component: AgentDashboardComponent, canActivate: [AgentGuard] },
  { path: 'agent/clients', component: ClientManagementComponent, canActivate: [AgentGuard] },
  { path: 'agent/transactions', component: AgentTransactionManagementComponent, canActivate: [AgentGuard] },
  { path: 'client', component: ClientDashboardComponent, canActivate: [ClientGuard] },
  { path: 'client/transfer', component: ClientTransferComponent, canActivate: [ClientGuard] },
  { path: 'client/mobile-recharge', component: MobileRechargeComponent, canActivate: [ClientGuard] },
  { path: 'client/notifications', component: ClientNotificationsComponent, canActivate: [ClientGuard] },
  { path: 'client/crypto-wallet', component: CryptoWalletPageComponent, canActivate: [ClientGuard] },
  { path: 'change-password', component: ChangePasswordComponent, canActivate: [AuthGuard] },
  { path: '**', redirectTo: '/login' }
];
