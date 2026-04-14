import request from '@/utils/request'

export function listLogs(query) {
  return request({
    url: '/api/logs/query',
    method: 'get',
    params: query
  })
}

export function getLogHistogram(query) {
  return request({
    url: '/api/logs/histogram',
    method: 'get',
    params: query
  })
}