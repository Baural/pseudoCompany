package com.fortebank.domain

trait DomainObject {

}

case class PseudoCompany (
    bin: String,
    courtDecision: Option[String],
    illegalActivityStartDate: Option[String],
    ownerIin: Option[String],
    ownerName: Option[String],
    ownerRnn: Option[String],
    rnn: Option[String],
    taxpayerName: Option[String],
    taxpayerOrganization: Option[String]
) extends  DomainObject