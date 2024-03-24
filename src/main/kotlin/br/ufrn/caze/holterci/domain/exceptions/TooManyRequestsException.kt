package br.ufrn.caze.holterci.domain.exceptions

class TooManyRequestsException(val m : String) : Exception(m)