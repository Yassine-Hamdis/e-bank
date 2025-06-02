import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();

    if (token) {
      const authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });

      // Debug logging for agent endpoints
      if (req.url.includes('/api/v1/agent/') || req.url.includes('/api/agent/')) {
        console.log('AuthInterceptor: Adding token to agent request');
        console.log('AuthInterceptor: Request URL:', req.url);
        console.log('AuthInterceptor: Token present:', !!token);
        console.log('AuthInterceptor: Token preview:', token ? token.substring(0, 20) + '...' : 'No token');
      }

      return next.handle(authReq);
    }

    // Debug logging for requests without token
    if (req.url.includes('/api/v1/agent/') || req.url.includes('/api/agent/')) {
      console.log('AuthInterceptor: No token available for agent request');
      console.log('AuthInterceptor: Request URL:', req.url);
    }

    return next.handle(req);
  }
}
