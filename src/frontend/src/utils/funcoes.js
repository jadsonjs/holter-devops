
/**
 *  Universidade Federal do Rio Grande do Norte
 *  Instituto Metrópole Digital
 *  Diretoria de Tecnologia da Informação
 *
 *
 *  Arquivo com funções de utilidades genéricas.
 *
 *  @author Jadson Santos - jadson.santos@ufrn.br
 *  @since  17/06/2021
 */

/**
 * Verifica se o objeto passado está vazio, nulo ou não foi declarado
 *
 * @param {obj} obj
 */
export const objectIsEmpty = (obj) => {
	return obj === null || obj === undefined || Object.keys(obj).length === 0
}

/**
 * Retorna string com apenas números (dígitos)
 * @param {any}
 */
export const apenasDigitos = (input) => {
	return input ? input.toString().replace(/\D/g, '') : ''
}


/**
 * Retira os acentos da string informada
 * @param {string} texto - O texto a ser tratado
 * @returns {string} O texto sem acentos
 */
export const removeAcentuacao = (texto) => {
	return texto.normalize('NFD').replace(/[\u0300-\u036f]/g, '')
}


/**
 * Verifica se o CPF informado é um CPF válido
 * Fonte: arquivo de funções em JS do site do Ministério da Fazenda
 * @param {number|string} cpf - CPF a ser verificado
 */
export const isCPFValido = (cpf) => {
	cpf = cpf11digitos(cpf)

	// Verifica se os dígitos não são todos iguais
	let regex = new RegExp(/0{11}|1{11}|2{11}|3{11}|4{11}|5{11}|6{11}|7{11}|8{11}|9{11}/g)
	if (cpf.match(regex)) return false

	// Primeiro dígito verificador
	let resto = 0
	let soma = 0
	for (let i = 1; i <= 9; i++) soma = soma + parseInt(cpf.substring(i - 1, i)) * (11 - i)
	resto = (soma * 10) % 11
	if (resto == 10 || resto == 11) resto = 0
	if (resto != parseInt(cpf.substring(9, 10))) return false

	// Segundo dígito verificador
	soma = 0
	resto = 0
	for (let i = 1; i <= 10; i++) soma = soma + parseInt(cpf.substring(i - 1, i)) * (12 - i)
	resto = (soma * 10) % 11
	if (resto == 10 || resto == 11) resto = 0
	if (resto != parseInt(cpf.substring(10, 11))) return false

	return true
}

/**
 * Insere zeros à esquerda do CPF caso não tenha 11 dígitos
 * @param {number|string} cpf - CPF a ser tratado
 */
const cpf11digitos = (cpf) => {
	let cpfStr = apenasDigitos(cpf)
	for (let i = cpfStr.length; i < 11; i++) cpfStr = '0' + cpfStr
	return cpfStr
}

/**
 * CNPJ formatado com os pontos e hífen
 * @param {string} cnpj - CNPJ a ser tratado
 */
export const isCNPJValido = (cnpj) => {
	cnpj = apenasDigitos(cnpj)

	// Verifica se os dígitos não são todos iguais
	let regex = new RegExp(/0{14}|1{14}|2{14}|3{14}|4{14}|5{14}|6{14}|7{14}|8{14}|9{14}/g)
	if (cnpj.match(regex)) return false

	// Primeiro dígito verificador
	let resto = 0
	let soma = 0
	let modificador = 6
	for (let i = 1; i <= 12; i++) {
		if (i > 4) modificador = -2
		soma = soma + parseInt(cnpj.substring(i - 1, i)) * (12 - i - modificador)
	}
	resto = soma % 11
	if (resto < 2) resto = 11
	if (11 - resto != parseInt(cnpj.substring(12, 13))) return false

	// Segundo dígito verificador
	soma = 0
	resto = 0
	modificador = 6
	for (let i = 1; i <= 13; i++) {
		if (i > 5) modificador = -2
		soma = soma + parseInt(cnpj.substring(i - 1, i)) * (13 - i - modificador)
	}
	resto = soma % 11
	if (resto < 2) resto = 11
	if (11 - resto != parseInt(cnpj.substring(13, 14))) return false

	return true
}

/**
 * Verifica se a data informada é válida
 * @param {string} data - Data a ser verificada no formato "dd/mm/yyyy"
 */
export const isDataValida = (data) => {
	let [dia, mes, ano] = data.split('/')
	mes = parseInt(mes)

	// Está no formato dd/mm/yyyy
	if (data.match(/\d{2}\/\d{2}\/\d{4}/) === null) return false

	// Mês válido
	if (mes < 1 || mes > 12) return false

	// Dia do mês válido
	let diasNoMes
	switch (mes) {
		case 2:
			diasNoMes = (ano % 4 == 0 && ano % 100) || ano % 400 == 0 ? 29 : 28
			break
		case 4:
		case 6:
		case 9:
		case 11:
			diasNoMes = 30
			break
		default:
			diasNoMes = 31
	}
	if (diasNoMes < dia || dia <= 0) return false

	return true
}

/**
 * Verifica se a string informada é não-vazia
 * @param {string} texto - Texto a ser verificado
 */
export const isStringValida = (texto) => {
	return texto && texto.trim() !== ""
}

/**
 * Oculta o CPF informado
 *
 * @param {number|string} cpf - CPF a ser ocultado
 */
export const ocultarCPF = (cpf) => {
	// Apenas os números do CPF e com tamanho de 11 dígitos
	cpf = cpf11digitos(cpf)

	// Rasura os 3 primeiros dígitos e os de verificação
	return cpf
		.replace(/(\d{3})(\d)/, '***.$2')
		.replace(/(\d{3})(\d)/, '$1.$2')
		.replace(/(\d{3})(\d{1,2})$/, '$1-**')
}


/**
 * Oculta o CNPJ informado
 *
 * @param {number|string} cnpj - CNPJ a ser ocultado
 */
export const ocultarCNPJ = (cnpj) => {
	// Rasura 7 dígitos no meio do CNPJ
	return cnpj
	.replace(/(\d{2})\.\d{3}\.\d{3}\//, '$1.***.***/')
  .replace(/(\d{4})\-(\d{2})$/, '****-$2');
}

/**
 * Oculta o e-mail informado
 *
 * @param {string} email - E-mail a ser ocultado
 * @returns {string} E-mail apenas com os primeiros e últimos caracteres visíveis da parte do nome de usuário. Ex.: abcdefg@gmail.com -> ab***fg@gmail.com
 */
export const ocultarEmail = (email) => {
	if (!email) return ''

	let nome = email.toString().match(/(.+?)(?=@)/gi)?.[0] || ''

	let qtdChars = 0
	if (nome.length > 3 && nome.length < 7) qtdChars = 1
	if (nome.length >= 7) qtdChars = 2
	if (nome.length >= 11) qtdChars = 3

	let regex = new RegExp(`^(.{${qtdChars}})(.*)(.{${qtdChars}}@.*)$`)

	return email.toString().replace(regex, (_, a, b, c) => a + b.replace(/./g, '*') + c)
}

/**
 * Função para ocultar datas de nascimento
 * @param {string} data - String com data que será ocultada
 */
export const ocultarDataNascimento = (data) => {
	if (!data) return ''

	return data.toString().replace(/^(.{3})(.{3})(.*)$/, (_, a, b, c) => a + b + c.replace(/./g, '*'))
}

/**
 * Função genérica para ocultar metade de uma string
 * @param {string} input - String que terá metade ocultada
 */
export const ocultarTexto = (input) => {
	if (!input) return ''

	let strInput = input.toString()
	let strInputLength = strInput.length
	let inputOculto = strInput.substring(0, strInputLength / 2)
	for (let index = strInputLength / 2; index < strInputLength; index++) {
		inputOculto += '*'
	}

	return inputOculto
}




