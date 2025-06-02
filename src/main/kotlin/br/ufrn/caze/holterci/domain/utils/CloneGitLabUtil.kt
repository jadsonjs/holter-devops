package br.ufrn.caze.holterci.domain.utils

import br.ufrn.caze.holterci.domain.models.metric.Project
import br.ufrn.caze.holterci.domain.ports.repositories.crud.ProjectRepository
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.eclipse.jgit.errors.NoRemoteRepositoryException
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

@Component
class CloneGitLabUtil(
    private val projectRepository: ProjectRepository,
) {


    /**
     * Clones the project (or pull latest changes) to a local path.
     *
     * @param project the project to clone or pull (update)
     * @param localRepo the directory to clone the repository or pull changes
     * @return String with the local path to the project
     */
    fun cloneOrUpdateRepository(project: Project, localRepo: String): String {
        val projectFullName = "${project.organization}/${project.name}"
        val projectConfiguration = projectRepository.findConfigurationByIdProject(project.id!!)

        require(projectConfiguration.mainRepository.url.isNotBlank()) { "Main repository not created yet for project: '${project.name}'" }

        val remoteUrl = "${projectConfiguration.mainRepository.url}/${projectFullName}.git"
        println("URL: $remoteUrl")

        val token = projectConfiguration.mainRepository.token
        val credentialsProvider = UsernamePasswordCredentialsProvider("oauth2", token)

        // Check if project directory already exists (clone) or not (pull)
        val localRepoPath = Path.of(localRepo).toAbsolutePath().normalize()

        return if (!Files.exists(localRepoPath)) {
            // Clone
            Files.createDirectories(localRepoPath)
            performClone(remoteUrl, localRepo, credentialsProvider)
        } else {
            // Pull
            performPull(localRepo, credentialsProvider)
            localRepo
        }
    }

    private fun performClone(remoteUrl: String, localRepo: String, credentialsProvider: CredentialsProvider): String {
        println("Cloning repository: $remoteUrl to: $localRepo")
        try {
            Git.cloneRepository()
                .setURI(remoteUrl)
                .setDirectory(File(localRepo))
                .setCredentialsProvider(credentialsProvider)
                .call()
            println("Finished cloning.")
        } catch (ex: GitAPIException) {
            // Handle SSL error by skipping SSL verification
            if (ex.message?.contains("cannot open git-upload-pack") == true) {
                createSkipSSLVerifyConfiguration(localRepo)
                throw RuntimeException("SSL Problem: Skipping SSL verification.")
            }
            throw RuntimeException("Error cloning repository for '$remoteUrl': ${ex.message}", ex)
        }
        return localRepo
    }

    private fun performPull(localRepo: String, credentialsProvider: CredentialsProvider) {
        println("Pulling latest changes for: $localRepo")
        var remoteUrl = ""
        try {
            val git = Git.open(File(localRepo))
            remoteUrl = git.repository.config.getString("remote", "origin", "url")
            val pullResult = git.pull()
                .setCredentialsProvider(credentialsProvider)
                .call()
            println("Pull result: ${pullResult.mergeResult?.mergeStatus}")
            git.close()
            println("Finished pulling.")
        } catch (ex: InvalidRemoteException) {
            if (ex.cause is NoRemoteRepositoryException) {
//                ex.printStackTrace()
                throw RuntimeException(
                    "Invalid URL for remote repository: '$remoteUrl'. Check the project configuration.",
                    ex
                )
            }
        } catch (ex: GitAPIException) {
            ex.printStackTrace()
            throw RuntimeException("Error pulling latest changes for '$localRepo': ${ex.message}", ex)
        }
    }

    private fun createSkipSSLVerifyConfiguration(localRepo: String) {
        // This method creates the .git/config file to skip SSL verification
        val localGitConfig = "$localRepo/.git/config"
        val localGitConfigFile = File(localGitConfig)

        if (!localGitConfigFile.exists()) {
            localGitConfigFile.parentFile.mkdirs()
        }

        try {
            FileWriter(localGitConfigFile, true).use { writer ->
                writer.write("[http]\n")
                writer.write("\tsslVerify = false\n")
            }
        } catch (e: IOException) {
            throw RuntimeException(
                "Error creating .git config file to skip SSL verification for '$localRepo': ${e.message}",
                e
            )
        }
    }
}
