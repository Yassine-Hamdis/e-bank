import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ClientSidebarComponent } from '../client-sidebar/client-sidebar.component';
import { AuthService } from '../../services/auth.service';
import { CryptoWalletComponent } from '../crypto-wallet/crypto-wallet.component';
import { NotificationBellComponent } from '../notification-bell/notification-bell.component';

@Component({
  selector: 'app-crypto-wallet-page',
  imports: [CommonModule, CryptoWalletComponent, NotificationBellComponent, ClientSidebarComponent],
  templateUrl: './crypto-wallet-page.component.html',
  styleUrl: './crypto-wallet-page.component.css'
})
export class CryptoWalletPageComponent {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  goBack(): void {
    this.router.navigate(['/client']);
  }

  navigateToTransfer(): void {
    this.router.navigate(['/client/transfer']);
  }

  navigateToMobileRecharge(): void {
    this.router.navigate(['/client/mobile-recharge']);
  }

  navigateToNotifications(): void {
    this.router.navigate(['/client/notifications']);
  }

  navigateToDashboard(): void {
    this.router.navigate(['/client']);
  }
}
