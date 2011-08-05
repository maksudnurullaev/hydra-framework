from django.db import models
from datetime import datetime

class Chat(models.Model):
    message         = models.CharField(max_length=200)
    pub_date        = models.DateTimeField()
    activated       = models.BooleanField(default=False)
    activation_code = models.CharField(max_length=10)
    mobile_number   = models.CharField(max_length=15)

    def __unicode__(self):
        return self.message

    def was_published_today(self):
        return self.pub_date.date() == datetime.date.today()

    was_published_today.short_description = 'Published today?'

