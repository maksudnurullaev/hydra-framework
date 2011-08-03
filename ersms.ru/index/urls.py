from django.conf.urls.defaults import *
#import views 

#urlpatterns = patterns('',
#   (r'^$', views.index),
#    (r'^$', views.index),
#)

from django.views.generic.simple import direct_to_template

urlpatterns = patterns('',
    (r'^$', direct_to_template, {'template': 'index/index.html'}),
)

