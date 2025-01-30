{{/*
Create labels
*/}}
{{- define "orderflow.labels" -}}
app.kubernetes.io/name: '{{ required "" .Values.app.application }}'
app.kubernetes.io/instance: '{{ required "" .Values.app.instance }}'
app.kubernetes.io/version: '{{ required "" .Values.app.version }}'
{{- end -}}

{{/*
Create selectors
*/}}
{{- define "orderflow.selectors" -}}
app.kubernetes.io/name: '{{ required "" .Values.app.application }}'
app.kubernetes.io/instance: '{{ required "" .Values.app.instance }}'
{{- end -}}
