from django.conf.urls.defaults import *

urlpatterns = patterns('',
    (r'^$', 'chat.views.index'),
)