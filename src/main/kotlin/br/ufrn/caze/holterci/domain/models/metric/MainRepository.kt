package br.ufrn.caze.holterci.domain.models.metric

import jakarta.persistence.*
import jakarta.validation.constraints.NotEmpty

@Entity
@Table(name = "main_repository", schema = "holter")
class MainRepository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null;

    @NotEmpty
    @Column(name = "name", nullable = false)
    var name: String = ""

    /**
     * Repository API URl. used to collect the data.
     * No necessary if the project is in the GitHub, bacause the GitHub has just one URL.
     * But is necessary, for example, if the project are in local gitlab repository
     */
    @NotEmpty
    @Column(name = "url", nullable = false)
    var url: String = ""

    /**
     * Security token used by the most of the repository to give access to API.
     */
    @NotEmpty
    @Column(name = "token", nullable = false)
    var token: String = ""

    /**
     * The name of the branch that contains the production code.
     * By default, the branch "master" is the projection branch
     */
    @NotEmpty
    @Column(name = "prodution_branch", nullable = false)
    var produtionBranch: String = ""

    /**
     * List Labels separated by comma that indicate in the project that an issue is an error issue
     * Normally if this way we identify an error issue, by the label.
     */
    @Column(name = "issues_erros_labels", nullable = true)
    var issuesErrosLabels: String? = ""


    constructor(){
    }

    constructor(id : Long){
        this.id = id
    }
}