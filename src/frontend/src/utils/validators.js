/**
*  Universidade Federal do Rio Grande do Norte
*  Instituto Metrópole Digital
*  Diretoria de Tecnologia da Informação
*
*  Defining Global Validators
*
*  @author Jadson Santos - jadson.santos@ufrn.br
*  
*  @since  20/08/2024
*/
import { defineRule } from 'vee-validate';


defineRule('required', value => {
  if (value === null || value === undefined || value === '') {
    return 'O campo é obrigatório';
  }
  return true;
});


defineRule('numeric', value => {
  if (!/^\d+$/.test(value)) {
    return 'O campo deve conter apenas números naturais';
  }
  return true;
});

defineRule('decimal', value => {
  if (!/^\d+(\.\d+)?$/.test(value)) {
    return 'O campo deve conter apenas números positivos';
  }
  return true;
});

defineRule('email', value => {
  // Field is empty, should pass
  if (!value || !value.length) {
    return true;
  }
  // Check if email
  if (!/[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,4}/i.test(value)) {
    return 'E-mail inválido';
  }
  return true;
});


defineRule('length', (value, [size]) => {
  // The field is empty so it should pass
  if (!value || !value.length) {
    return true;
  }
  if (value.length !== Number(size)) {
    return `O campo deve ter o tamanho: ${size}`;
  }
  return true;
});


defineRule('minMax', (value, [min, max]) => {
  // The field is empty so it should pass
  if (!value || !value.length) {
    return true;
  }
  const numericValue = Number(value);
  if (numericValue < min) {
    return `O valor do campo de ser maior que: ${min}`;
  }else{
  	if (numericValue > max) {
    	return `O valor do campo deve ser menor que: ${max}`;
  	}
	}
  return true;
});


defineRule('minLength', (value, [limit]) => {
  // The field is empty so it should pass
  if (!value || !value.length) {
    return true;
  }
  if (value.length < limit) {
    return `O campo deve ter pelo menos ${limit} caracteres`;
  }
  return true;
});


defineRule('maxLength', (value, [limit]) => {
  // The field is empty so it should pass
  if (!value || !value.length) {
    return true;
  }
	if (value.length > limit) {
		return `O campo deve ter no máximo ${limit} caracteres`;
	}
  return true;
});


defineRule('minMaxLength', (value, [min, max]) => {
  // The field is empty so it should pass
  if (!value || !value.length) {
    return true;
  }
  if (value.length < min) {
    return `O campo deve ter pelo menos ${min} caracteres`;
  }else{
  	if (value.length > max) {
    	return `O campo deve ter no máximo ${max} caracteres`;
  	}
	}
  return true;
});

/**
 * Cross-Field Validation
 * 
 * <Field name="password" rules="required" />
 * <Field name="confirmation" rules="required|confirmed:password" />
 */
defineRule('confirmed', (value, [target], ctx) => {
  if (value === ctx.form[target]) {
    return true;
  }
  return 'As senhas devem coincidir';
});