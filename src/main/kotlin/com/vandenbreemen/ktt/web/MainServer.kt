package com.vandenbreemen.ktt.web

import com.vandenbreemen.ktt.interactor.ConfigurationInteractor
import com.vandenbreemen.ktt.interactor.StaticContentInteractor
import com.vandenbreemen.ktt.message.NoSuchPageError
import com.vandenbreemen.ktt.model.Page
import com.vandenbreemen.ktt.model.StylesheetType
import com.vandenbreemen.ktt.presenter.WikiPresenter
import com.vandenbreemen.ktt.view.PageRenderingInteractor
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

val logger = LoggerFactory.getLogger("MainServer")

internal fun startServer(staticContentInteractor: StaticContentInteractor, configInteractor: ConfigurationInteractor,
                renderingInteractor: PageRenderingInteractor, presenter: WikiPresenter
                ) {



    val port = configInteractor.getPort()
    logger.info("Running on port $port")

    embeddedServer(Netty, port) {
        routing {

            setupStaticContent(staticContentInteractor)

            mainPage(presenter, configInteractor)
            config(configInteractor)

            viewPage(presenter, renderingInteractor)
            undoChange(presenter)

            editPage(presenter)

            submitPageEdit(presenter)

            submitCreatedPage(presenter, logger)

            createPage(presenter)

            searchPage(presenter)

            submitSearch(presenter)

            customCssTooling(presenter)
        }
    }.start(wait = true)

}

private fun Routing.setupStaticContent(staticContentInteractor: StaticContentInteractor) {
    staticContentInteractor.setupDirectory()
    staticFiles(staticContentInteractor.staticContentWebPath, File(staticContentInteractor.staticContentRoot))
}

private fun Routing.customCssTooling(presenter: WikiPresenter) {
    post("/setup/css/{type}") {
        context.parameters["type"]?.let { pageType ->

            val type = StylesheetType.valueOf(pageType)

            call.receiveParameters().let { params ->
                val css = params["css"]

                if (css.isNullOrBlank()) {
                    call.respond(HttpStatusCode.BadRequest, "Please specify css for ${type.name}")
                } else {
                    presenter.updateCssByType(type, css)
                    call.respondRedirect("/")
                }
            }
        }
    }

    get("/setup/css/{type}") {
        context.parameters["type"]?.let { pageType ->

            val type = StylesheetType.valueOf(pageType)
            val existingCss = presenter.getCssByType(type)

            context.respondText(contentType = ContentType.Text.Html) {
                return@respondText StringBuilder().appendHTML().html {
                    head {
                        style {
                            unsafe {
                                raw(presenter.css)
                            }
                        }
                    }

                    body {
                        h1 {
                            +"${type.name} css"
                        }
                        p {
                            +"CSS class name for this item is"
                        }
                        p {
                            +"${type.cssClass}"
                        }
                        form(action = "/setup/css/${type.name}", method = FormMethod.post) {
                            textArea(wrap = TextAreaWrap.hard) {
                                name = "css"
                                contentEditable = true
                                autoFocus = true
                                text(existingCss)
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
                }.toString()
            }
        }
    }
}

/**
 * Store configuration updates
 */
private fun Routing.config(configurationInteractor: ConfigurationInteractor) {
    post("/config") {
        call.receiveParameters()?.let { parameters ->

            val currentConfig = configurationInteractor.getUIConfiguration()

            try {
                parameters["port"]?.let { port ->
                    with(currentConfig.copy(runPort = port.toInt())) {
                        configurationInteractor.updateUIConfiguration(this)
                    }
                }

                call.respondRedirect("/")
            } catch (e: Exception) {
                context.respondText(contentType = ContentType.Text.Html) {
                    StringBuilder().appendHTML().html {
                        body {
                            div(classes = Classes.errorSection) {
                                +(e.message ?: "Unknown error")
                            }
                        }
                    }.toString()
                }
            }
        }
    }
}

private fun Routing.mainPage(presenter: WikiPresenter, configurationInteractor: ConfigurationInteractor) {
    get("/") {

        val currentPort = configurationInteractor.getPort()

        this.context.respondText(contentType = ContentType.Text.Html) {
            return@respondText StringBuilder().appendHTML().html {
                head {
                    style {
                        unsafe {
                            raw(presenter.css)
                        }
                    }
                }
                body {
                    div(classes = Classes.wikiEntry) {
                        h1 {
                            +"Welcome Back"
                        }
                        p {
                            +"This is your personal wiki.  You can use it to store information etc.  Enough from me, why not head on over to your main page?"
                        }
                        h2 {
                            try {
                                val firstPage = presenter.fetchPage("1")
                                a(href = "/page/1") {
                                    +"${firstPage.title}"
                                }
                            } catch (pnf: NoSuchPageError) {
                                a(href = "/page/create/Main Page") {
                                    +"Create your first page"
                                }
                            }
                        }
                        h2 {
                            +"Configuration"
                        }
                        div(classes = Classes.controlPanel) {
                            h3 {
                                +"App Config"
                            }
                            form(action = "/config", method = FormMethod.post) {
                                table {
                                    tr {
                                        td {
                                            +"Website Port (requires restart)"
                                        }
                                        td {
                                            input(name = "port", type = InputType.number) {
                                                value = currentPort.toString()
                                            }
                                        }
                                    }
                                }
                                button(type = ButtonType.submit) {
                                    +"Apply"
                                }
                            }
                            h3 {
                                +"CSS Config"
                            }
                            ul {
                                StylesheetType.values().forEach {type->
                                    li {
                                        a(href = "/setup/css/${type.name}") {
                                            +"${type.name}"
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }.toString()
        }
    }
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
                                raw(presenter.css)
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

private fun Routing.searchPage(presenter: WikiPresenter) {
    get("/search") {
        context.respondText(contentType = ContentType.Text.Html) {
            StringBuilder().appendHTML().html {
                head {
                    style {
                        unsafe {
                            raw(presenter.css)
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

private fun buildEditor(presenter: WikiPresenter, title: String, existingPageContent: String?, pageId: String?, existingPageTags: String = ""): String {
    return StringBuilder().appendHTML().html {
        head {
            style {
                unsafe {
                    raw(presenter.css)
                }
            }
        }

        body {
            div(classes = Classes.editor) {
                form(action = if(existingPageContent != null) "/edit/${pageId ?: ""}" else "/page/create", method = FormMethod.post) {
                    h3 {
                        +"Title"
                    }
                    input(name = "title", type = InputType.text) {
                        value = title
                        readonly=true
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
                    textArea(wrap = TextAreaWrap.hard) {
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

private fun Routing.createPage(presenter: WikiPresenter) {
    get("/page/create/{title}") {
        context.parameters["title"]?.let { title ->
            context.respondText(contentType = ContentType.Text.Html) {
                buildEditor(presenter, title, null, null)
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
                    buildEditor(presenter, page.title, page.content, pageId, tags)
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

private fun Routing.undoChange(presenter: WikiPresenter) {
    get("/page/undo/{pageId}") {
        context.parameters["pageId"]?.let { pageId ->
            presenter.undoLastChange(pageId.toInt())
            context.respondRedirect("/page/$pageId")
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
                                    raw(presenter.css)
                                }
                            }
                        }
                        body {
                            presenter.breadcrumbTrail.run {
                                if(!isEmpty()) {
                                    div(classes = Classes.breadcrumbs) {
                                        forEach { breadcrumb->
                                            a {
                                                href="/page/${breadcrumb.pageId}"
                                                +"${breadcrumb.title} >> "
                                            }
                                        }
                                    }
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
                                a {
                                    accessKey = "h"
                                    href = "/"
                                    +"HOME"
                                }
                                if(presenter.hasPreviousVersion(pageId.toInt())) {
                                    a {
                                        accessKey = "u"
                                        href = "/page/undo/$pageId"
                                        +"UNDO LAST CHANGE"
                                    }
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