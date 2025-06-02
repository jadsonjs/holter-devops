/**
 * Universidade Federal do Rio Grande do Norte
 * Instituto Metrópole Digital
 * Diretoria de Tecnologia da Informação
 *
 *
 * Guarda os dados para realizar o contador em caso do login falar
 *
 * PS.: Nao pode usar localstorage aqui pq ele é apagado depois que o login fala
 *
 * @author Jadson Santos - jadson.santos@ufrn.br
 * @since  25/11/2024
 */
import { reactive } from 'vue';
import Cookies from 'js-cookie';

const storeID = 'locktime-login'
const appName = import.meta.env.VITE_APP_PUBLIC_PATH.replace(/[/]+/g, '')

const failedAttemptsName = `${appName}-${storeID}-failed`;
const lockTimeRemainingName =  `${appName}-${storeID}-lockTime`;


export const lockTimeLoginStore = reactive({

	failedAttempts:    parseInt(Cookies.get(failedAttemptsName))    || 0,
	lockTimeRemaining: parseInt(Cookies.get(lockTimeRemainingName)) || 0,
  timerInterval: null, // Store the interval reference

  setFailedAttempts(value) {
    this.failedAttempts = parseInt(value);
		Cookies.set(failedAttemptsName, this.failedAttempts, { secure: true, sameSite: 'Strict' });

  },

  setLockTimeRemaining(value) {
    this.lockTimeRemaining = parseInt(value);
		Cookies.set(lockTimeRemainingName, this.lockTimeRemaining, { secure: true, sameSite: 'Strict' });
  },

  // Action to increment failed attempts
  registerFailedAttempt() {
    const INITIAL_LOCKOUT_TIME = 30; // 30 seconds
    const MAX_LOCKOUT_TIME = 3600;  // 60 minutes in seconds

    this.setFailedAttempts(Math.min(this.failedAttempts + 1, 7));
    const calculatedLockTime = Math.pow(2, this.failedAttempts - 1) * INITIAL_LOCKOUT_TIME;
    this.setLockTimeRemaining(Math.min(calculatedLockTime, MAX_LOCKOUT_TIME));

    // Start the countdown timer
    this.startCountDownLockTimer();
  },

  // Action to start the countdown timer
  startCountDownLockTimer() {
    if (this.lockTimeRemaining > 0 && !this.timerInterval) {
      this.timerInterval = setInterval(() => {
        this.setLockTimeRemaining(this.lockTimeRemaining - 1);
        if (this.lockTimeRemaining <= 0) {
          clearInterval(this.timerInterval);
          this.timerInterval = null;
        }
      }, 1000);
    }
  },

  // Action to reset the timer interval if needed (optional)
  resetFailedAttempts() {
    this.setFailedAttempts(0);
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
  }

});