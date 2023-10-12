package com.vandenbreemen.ktt.web

import com.vandenbreemen.ktt.interactor.MarkdownInteractor
import com.vandenbreemen.ktt.interactor.TestWikiInteractor
import com.vandenbreemen.ktt.interactor.WikiInteractor
import com.vandenbreemen.ktt.interactor.WikiPageTagsInteractor
import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import com.vandenbreemen.ktt.presenter.WikiPresenter
import com.vandenbreemen.ktt.view.PageRenderingInteractor
import com.vandenbreemen.ktt.view.PageRenderingPluginRegistry
import com.vandenbreemen.ktt.view.plugins.PageLinkPlugin
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {

    val repository = SQLiteWikiRepository(("main.db"))

    val registry = PageRenderingPluginRegistry().also {//  TODO    Make initialization and registration more formal
        it.register(PageLinkPlugin(repository))
    }


    val logger = LoggerFactory.getLogger("MainServer")

    val renderingInteractor = PageRenderingInteractor(MarkdownInteractor(), registry)

    val presenter = WikiPresenter( WikiInteractor(TestWikiInteractor(), repository), WikiPageTagsInteractor(repository))

    embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                this.context.respondText(contentType = ContentType.Text.Html) {
                    return@respondText StringBuilder().appendHTML().html {
                        body {
                            div {
                                h1 {
                                    +"Main Home Page"
                                }
                                p {
                                    +"To view a wiki page try path /page/[id]"
                                }
                            }
                        }
                    }.toString()
                }
            }

            viewPage(presenter, renderingInteractor)

            editPage(presenter)

            submitPageEdit(presenter)

            submitCreatedPage(presenter, logger)

            createPage()

            searchPage()

            submitSearch(presenter)
        }
    }.start(wait = true)

}

private fun Routing.submitSearch(presenter: WikiPresenter) {
    post("/search") {
        call.receiveParameters().let { parameters ->
            val searchTerm = parameters["searchTerm"] ?: ""

            context.respondText(contentType = ContentType.Text.Html) {
                StringBuilder().appendHTML().html {
                    head {
                        style {
                            unsafe {
                                raw(css)
                            }
                        }
                    }
                    body {
                        div(classes = Classes.topSection) {
                            a {
                                onClick = "history.back()"
                                +"Back"
                            }
                        }
                        div(classes = Classes.resultsSection) {
                            try {
                                val result = presenter.searchPage(searchTerm)
                                result.forEach { item ->
                                    div(classes = Classes.item) {
                                        a(href = "/page/${item.pageId}") {
                                            +item.pageTitle
                                        }
                                    }
                                }

                            } catch (ex: Exception) {
                                div(classes = Classes.errorSection) {
                                    +(ex.message ?: "Unknown error")
                                }
                            }

                        }
                    }
                }.toString()
            }
        }

    }
}

private fun Routing.searchPage() {
    get("/search") {
        context.respondText(contentType = ContentType.Text.Html) {
            StringBuilder().appendHTML().html {
                head {
                    style {
                        unsafe {
                            raw(css)
                        }
                    }
                }

                body {
                    div(classes = Classes.topSection) {
                        a {
                            accessKey = "c"
                            onClick = "history.back()"
                            +"Back"
                        }
                    }
                    div(classes = Classes.editor) {
                        form(action = "/search", method = FormMethod.post) {
                            input(name = "searchTerm", type = InputType.text) {
                                autoFocus = true
                            }
                            button(name = "SEARCH", type = ButtonType.submit) {
                                accessKey = "s"
                                text("SEARCH")
                            }
                        }

                    }
                }
            }.toString()
        }
    }
}

private fun buildEditor(title: String, existingPageContent: String?, pageId: String?, existingPageTags: String = ""): String {
    return StringBuilder().appendHTML().html {
        head {
            style {
                unsafe {
                    raw(css)
                }
            }
        }

        body {
            div(classes = Classes.topSection) {
                p {
                    +"Return to Page"
                }
            }
            div(classes = Classes.editor) {
                form(action = if(existingPageContent != null) "/edit/${pageId ?: ""}" else "/page/create", method = FormMethod.post) {
                    h3 {
                        +"Title"
                    }
                    input(name = "title", type = InputType.text) {
                        value = title
                    }
                    h3 {
                        +"Tags"
                    }
                    input(name="tags", type = InputType.text) {
                        value = existingPageTags
                    }
                    h3 {
                        +"Content"
                    }
                    textArea(wrap = TextAreaWrap.soft) {
                        name = "content"
                        contentEditable = true
                        autoFocus = true
                        existingPageContent?.let { content->
                            text(content)
                        }
                    }
                    div(classes = Classes.controlPanel) {
                        button(name = "submit", type = ButtonType.submit) {
                            accessKey = "s"
                            text("SAVE")
                        }
                        button(name = "cancel", type = ButtonType.button) {
                            onClick = "history.back()"
                            accessKey = "c"
                            text("CANCEL")
                        }
                    }
                }

            }
        }
    }.toString()
}

private fun Routing.createPage() {
    get("/page/create/{title}") {
        context.parameters["title"]?.let { title ->
            context.respondText(contentType = ContentType.Text.Html) {
                buildEditor(title, null, null)
            }
        }
    }
}

private fun Routing.submitCreatedPage(
    presenter: WikiPresenter,
    logger: Logger
) {
    post("/page/create") {
        call.receiveParameters().let { params ->
            val title = params["title"]
            val content = params["content"]
            val tags = params["tags"] ?: ""

            try {
                val pageId = presenter.createPage(Page(title ?: "", content ?: ""))
                presenter.handlePageTags(pageId.toString(), tags)
                call.respondRedirect("/page/$pageId")
            } catch (e: Exception) {
                logger.error("Error occurred creating page", e)
                context.respondText(
                    "Error:  ${e.message}",
                    contentType = ContentType.Text.Html,
                    status = HttpStatusCode(400, e.message ?: "")
                )
            }
        }
    }
}

private fun Routing.submitPageEdit(presenter: WikiPresenter) {
    post("/edit/{pageId}") {
        context.parameters["pageId"]?.let { pageId ->
            call.receiveParameters().let { params ->
                val title = params["title"]
                val content = params["content"]
                val tags = params["tags"] ?: ""

                try {
                    presenter.updatePage(pageId, Page(title ?: "", content ?: ""))
                    presenter.handlePageTags(pageId, tags)
                    call.respondRedirect("/page/$pageId")
                } catch (exception: Exception) {
                    context.respondText(
                        "Page not found",
                        contentType = ContentType.Text.Html,
                        status = HttpStatusCode(404, "No wiki page found with id $pageId")
                    )
                }
            }
        }
    }
}

private fun Routing.editPage(presenter: WikiPresenter) {
    get("/edit/{pageId}") {
        context.parameters["pageId"]?.let { pageId ->
            try {
                val page = presenter.fetchPage(pageId)
                val tags = presenter.getTags(pageId)

                context.respondText(contentType = ContentType.Text.Html) {
                    buildEditor(page.title, page.content, pageId, tags)
                }
            } catch (e: Exception) {
                context.respondText(
                    "Page not found",
                    contentType = ContentType.Text.Html,
                    status = HttpStatusCode(404, "No wiki page found with id $pageId")
                )
            }
        }
    }
}

private fun Routing.viewPage(
    presenter: WikiPresenter,
    renderingInteractor: PageRenderingInteractor
) {
    get("/page/{pageId}") {
        context.parameters["pageId"]?.let { pageId ->
            try {
                val page = presenter.fetchPage(pageId)
                presenter.onViewPage(pageId, page)

                context.respondText(contentType = ContentType.Text.Html) {
                    StringBuilder().appendHTML().html {
                        head {
                            style {
                                unsafe {
                                    raw(css)
                                }
                            }
                        }
                        body {
                            div(classes = Classes.topSection) {
                                p {
                                    +"Return Home"
                                }
                            }
                            div(classes = Classes.wikiEntry) {
                                this.htmlObject {
                                    unsafe {
                                        this.raw(
                                            renderingInteractor.render(page)
                                        )
                                    }
                                }
                            }
                            div(Classes.controlPanel) {
                                a {
                                    accessKey = "e"
                                    href = "/edit/$pageId"
                                    +"EDIT"
                                }
                                a {
                                    accessKey = "f"
                                    href = "/search"
                                    +"SEARCH"
                                }
                            }
                        }
                    }.toString()
                }
            } catch(e: Exception) {
                context.respondText(
                    "Page not found",
                    contentType = ContentType.Text.Html,
                    status = HttpStatusCode(404, "No wiki page found with id $pageId")
                )
            }

        }

    }
}