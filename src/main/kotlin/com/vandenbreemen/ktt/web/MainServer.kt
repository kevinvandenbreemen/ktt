package com.vandenbreemen.ktt.web

import com.vandenbreemen.ktt.interactor.MarkdownInteractor
import com.vandenbreemen.ktt.interactor.TestWikiInteractor
import com.vandenbreemen.ktt.interactor.WikiInteractor
import com.vandenbreemen.ktt.persistence.SQLiteWikiRepository
import com.vandenbreemen.ktt.view.PageRenderingInteractor
import io.ktor.http.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.slf4j.LoggerFactory

fun main(args: Array<String>) {

    val logger = LoggerFactory.getLogger("MainServer")

    val interactor = WikiInteractor(TestWikiInteractor(), SQLiteWikiRepository(("main.db")))
    val renderingInteractor = PageRenderingInteractor(MarkdownInteractor())

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

            get("/page/{pageId}") {
                context.parameters["pageId"]?.let { pageId->
                    interactor.fetchPage(pageId)?.let { page->

                        context.respondText(contentType = ContentType.Text.Html) {
                            StringBuilder().appendHTML().html {
                                body {
                                    div {
                                        p {
                                            +"Return Home"
                                        }
                                    }
                                    div {
                                        this.htmlObject {
                                            unsafe {
                                                this.raw(
                                                    renderingInteractor.render(page)
                                                )
                                            }

                                        }

                                    }
                                }
                            }.toString()
                        }
                    } ?: run {
                        context.respondText("Page not found",
                            contentType = ContentType.Text.Html,
                            status = HttpStatusCode(404, "No wiki page found with id $pageId"))
                    }

                }

            }
        }
    }.start(wait = true)

}