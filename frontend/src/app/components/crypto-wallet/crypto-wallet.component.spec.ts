import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';

import { CryptoWalletComponent } from './crypto-wallet.component';
import { ClientService } from '../../services/client.service';
import {
  CryptoWalletDetails,
  CryptoBalanceDetail,
  CryptoRates,
  CryptoBuyFromMainRequest,
  CryptoTransactionResponse
} from '../../models/client.model';

describe('CryptoWalletComponent', () => {
  let component: CryptoWalletComponent;
  let fixture: ComponentFixture<CryptoWalletComponent>;
  let clientService: jasmine.SpyObj<ClientService>;
  let httpMock: HttpTestingController;

  const mockBalanceDetails: CryptoBalanceDetail[] = [
    {
      id: 1,
      cryptoType: 'ETH',
      balance: 0.39441042,
      valueInMAD: 1183.23,
      createdDate: '2025-05-31T16:00:20.334Z',
      updatedDate: null
    },
    {
      id: 2,
      cryptoType: 'USDT',
      balance: 2000.00000000,
      valueInMAD: 2000.00,
      createdDate: '2025-05-31T16:01:15.121Z',
      updatedDate: null
    }
  ];

  const mockWalletDetails: CryptoWalletDetails = {
    id: 2,
    walletAddress: 'crypto-wallet-address-123',
    clientId: 'CLT001',
    createdDate: '2025-05-01T10:30:20.334Z',
    updatedDate: '2025-05-30T15:45:10.123Z',
    status: 'ACTIVE',
    supportedCryptos: ['BTC', 'ETH', 'USDT', 'BNB'],
    lastAccessDate: '2025-05-31T17:07:22.839Z',
    cryptoBalances: {
      BTC: 0.00000000,
      ETH: 0.39441042,
      USDT: 2000.00000000,
      BNB: 0.00000000
    },
    balanceDetails: mockBalanceDetails,
    totalValueMAD: 3183.23,
    totalBalances: 2
  };

  const mockCryptoRates: CryptoRates = {
    BTC: 45000,
    ETH: 3000,
    USDT: 1,
    timestamp: Date.now()
  };

  const mockTransactionResponse: CryptoTransactionResponse = {
    transactionId: 'tx123',
    amount: 1000,
    type: 'CRYPTO_BUY',
    status: 'PENDING'
  };

  beforeEach(async () => {
    const clientServiceSpy = jasmine.createSpyObj('ClientService', [
      'getCryptoWalletDetails',
      'getCryptoRates',
      'getCryptoTransactions',
      'buyCryptoFromMain'
    ]);

    await TestBed.configureTestingModule({
      imports: [CryptoWalletComponent, HttpClientTestingModule, FormsModule],
      providers: [
        { provide: ClientService, useValue: clientServiceSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CryptoWalletComponent);
    component = fixture.componentInstance;
    clientService = TestBed.inject(ClientService) as jasmine.SpyObj<ClientService>;
    httpMock = TestBed.inject(HttpTestingController);

    // Setup default spy returns
    clientService.getCryptoWalletDetails.and.returnValue(of(mockWalletDetails));
    clientService.getCryptoRates.and.returnValue(of(mockCryptoRates));
    clientService.getCryptoTransactions.and.returnValue(of([]));
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with default values', () => {
    expect(component.selectedCrypto).toBe('BTC');
    expect(component.tradeType).toBe('buy');
    expect(component.platformFee).toBe(10.00);
    expect(component.useRealTimeRate).toBe(true);
  });

  it('should load crypto data on init', () => {
    component.ngOnInit();

    expect(clientService.getCryptoWalletDetails).toHaveBeenCalled();
    expect(clientService.getCryptoRates).toHaveBeenCalled();
    expect(clientService.getCryptoTransactions).toHaveBeenCalled();
  });

  it('should handle buy-from-main trade type', () => {
    component.tradeType = 'buy-from-main';
    component.selectedCrypto = 'BTC';
    component.tradeAmount = 1000;
    component.description = 'Test buy from main';
    component.platformFee = 15.00;
    component.useRealTimeRate = false;

    clientService.buyCryptoFromMain.and.returnValue(of(mockTransactionResponse));

    component.executeTrade();

    const expectedRequest: CryptoBuyFromMainRequest = {
      cryptoType: 'BTC',
      amount: 1000,
      description: 'Test buy from main',
      platformFee: 15.00,
      useRealTimeRate: false
    };

    expect(clientService.buyCryptoFromMain).toHaveBeenCalledWith(expectedRequest);
  });

  it('should use default description for buy-from-main when description is empty', () => {
    component.tradeType = 'buy-from-main';
    component.selectedCrypto = 'ETH';
    component.tradeAmount = 500;
    component.description = '';

    clientService.buyCryptoFromMain.and.returnValue(of(mockTransactionResponse));

    component.executeTrade();

    const expectedRequest: CryptoBuyFromMainRequest = {
      cryptoType: 'ETH',
      amount: 500,
      description: 'Buy ETH from main account',
      platformFee: 10.00,
      useRealTimeRate: true
    };

    expect(clientService.buyCryptoFromMain).toHaveBeenCalledWith(expectedRequest);
  });

  it('should handle buy-from-main error', () => {
    component.tradeType = 'buy-from-main';
    component.tradeAmount = 1000;

    clientService.buyCryptoFromMain.and.returnValue(throwError(() => new Error('API Error')));

    component.executeTrade();

    expect(component.errorMessage).toBe('Trade failed. Please try again.');
    expect(component.isTrading).toBe(false);
  });

  it('should reset form including new fields', () => {
    component.tradeAmount = 1000;
    component.walletAddress = 'test-address';
    component.description = 'test description';
    component.platformFee = 20.00;
    component.useRealTimeRate = false;

    component.resetTradeForm();

    expect(component.tradeAmount).toBe(0);
    expect(component.walletAddress).toBe('');
    expect(component.description).toBe('');
    expect(component.platformFee).toBe(10.00);
    expect(component.useRealTimeRate).toBe(true);
  });

  it('should calculate crypto rate correctly', () => {
    component.cryptoRates = mockCryptoRates;

    expect(component.getCryptoRate('BTC')).toBe(45000);
    expect(component.getCryptoRate('ETH')).toBe(3000);
    expect(component.getCryptoRate('USDT')).toBe(1);
  });
});
