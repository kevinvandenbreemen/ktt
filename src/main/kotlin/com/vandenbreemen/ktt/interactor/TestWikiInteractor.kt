package com.vandenbreemen.ktt.interactor

import com.vandenbreemen.ktt.model.Page

class TestWikiInteractor() {

    fun isTestID(id: String): Boolean {
        return id == "test"
    }

    fun getTestWiki(): Page {
        return Page(
            "Test Page",
            """
                ## This is a test of the wiki.  The following is some Lorem Ipsum
                
                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin convallis et odio quis tincidunt. Maecenas pharetra ultricies magna ut venenatis. Integer pretium enim in eros dapibus, ac porta justo pulvinar. Aenean ultrices nec augue vel hendrerit. In hac habitasse platea dictumst. Morbi at sem nec magna vehicula condimentum. Duis molestie blandit ex, a hendrerit magna sodales non. Vivamus et malesuada ante. Donec nibh est, hendrerit id dignissim sit amet, fringilla eu ligula. Mauris suscipit, dui et posuere volutpat, eros nunc bibendum elit, sit amet ullamcorper eros magna et nisi. Curabitur elit tellus, vehicula vitae arcu at, pharetra sollicitudin justo. Sed porta, orci at pharetra feugiat, massa leo feugiat eros, eu semper turpis turpis et urna. Nunc vulputate quam et ipsum consequat accumsan nec luctus mauris. Etiam ut convallis nisi. Etiam ultricies eros in ex blandit, eget lobortis nisl vehicula.

                Maecenas varius leo erat, at convallis enim tempor non. Sed dictum posuere odio id accumsan. Cras volutpat nulla eu eleifend blandit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Cras non mi nec ex scelerisque pharetra sit amet nec lectus. Nam ut ex eget mauris mollis auctor. Duis gravida volutpat aliquam. Ut tempor erat et ligula rhoncus scelerisque.

                Aenean porta diam aliquam pulvinar tristique. Aliquam erat volutpat. Phasellus sit amet felis convallis, aliquet nibh non, sagittis dolor. Maecenas in neque at mi fermentum vulputate. Aenean facilisis congue maximus. Maecenas accumsan dignissim sem. Nullam non magna felis. Morbi eget felis ac metus ullamcorper consectetur. Etiam viverra mauris eget diam vestibulum, bibendum auctor nunc auctor. Vivamus porttitor orci massa, et auctor libero efficitur sit amet. In molestie quam sed maximus consectetur. Quisque blandit ullamcorper imperdiet. Suspendisse potenti. Ut sit amet facilisis nunc.

                Morbi vitae velit orci. Sed id pulvinar justo, ut pharetra mi. Phasellus eget turpis quam. Nullam nisl lacus, fringilla a odio id, elementum lobortis mauris. Donec pulvinar orci sed ipsum faucibus, eget ullamcorper libero lobortis. Nunc vestibulum libero a tortor rhoncus, non elementum ante fringilla. In viverra maximus rutrum.

                Vestibulum condimentum ipsum eget massa mattis, dictum auctor diam ultricies. Suspendisse sit amet tellus ut mauris condimentum congue ut in est. Integer erat purus, pulvinar in tellus a, luctus molestie ante. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Nulla scelerisque pretium tincidunt. Aliquam in tellus metus. Vivamus magna mauris, pulvinar non mauris vel, varius imperdiet ex. Aliquam fringilla ornare bibendum. Quisque facilisis dignissim ante, et mattis enim. Fusce nec augue quis lorem vestibulum consequat vel non ex. Nulla fermentum magna sit amet urna pharetra ultricies. In hac habitasse platea dictumst. Nunc tempor dui sit amet urna mollis ultrices.
            """.trimIndent()
        )
    }


}