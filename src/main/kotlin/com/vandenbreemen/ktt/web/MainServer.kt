package com.vandenbreemen.ktt.web

import com.vandenbreemen.ktt.interactor.MarkdownInteractor
import com.vandenbreemen.ktt.interactor.TestWikiInteractor
import com.vandenbreemen.ktt.interactor.WikiInteractor
import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import com.vandenbreemen.ktt.presenter.WikiPresenter
import com.vandenbreemen.ktt.view.PageRenderingInteractor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {

    val logger = LoggerFactory.getLogger("MainServer")

    val renderingInteractor = PageRenderingInteractor(MarkdownInteractor())

    val presenter = WikiPresenter( WikiInteractor(TestWikiInteractor(), SQLiteWikiRepository(("main.db"))))

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

            post("/edit/{pageId}") {
                context.parameters["pageId"]?.let { pageId ->
                    call.receiveParameters().let { params ->
                        val title = params["title"]
                        val content = params["content"]

                        try {
                            presenter.updatePage(pageId, Page(title ?: "", content ?: ""))
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
    }.start(wait = true)

}

private fun Routing.editPage(presenter: WikiPresenter) {
    get("/edit/{pageId}") {
        context.parameters["pageId"]?.let { pageId ->
            try {
                val page = presenter.fetchPage(pageId)

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
                                    +"Return to Page"
                                }
                            }
                            div(classes = Classes.editor) {
                                form(action = "/edit/$pageId", method = FormMethod.post) {
                                    input(name = "title", type = InputType.text) {
                                        value = page.title
                                    }
                                    textArea(wrap = TextAreaWrap.soft) {
                                        name = "content"
                                        contentEditable = true
                                        text(page.content)
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