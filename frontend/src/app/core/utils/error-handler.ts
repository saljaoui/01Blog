export class ErrorHandler {
  static extractErrorMessage(err: any, defaultMessage = 'An error occurred. Please try again.'): string {
    if (err?.error && typeof err.error === 'object') {
      const firstError = Object.values(err.error)[0];
      if (typeof firstError === 'string') {
        return firstError;
      }
    }
    return defaultMessage;
  }
}
