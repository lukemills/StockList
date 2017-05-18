import unittest
from unittest import TestCase

import web_server


class TestUser(TestCase):
    def setUp(self):
        web_server.app.config['TESTING'] = True
        self.app = web_server.app.test_client()

    def tearDown(self):
        pass

    def test_get(self):
        self.app.get('/')
        self.fail()


if __name__ == '__main__':
    unittest.main()
