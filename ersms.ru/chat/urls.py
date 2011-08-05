from django.conf.urls.defaults import *
from django.views.generic import DetailView, ListView

urlpatterns = patterns('',
    (r'^$', 'chat.views.index'),
)
