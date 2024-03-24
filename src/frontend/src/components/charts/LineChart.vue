
<script>

import { Line } from 'vue-chartjs';

export default {

  name: 'LineChart',

  extends: Line,

  props: ['data', 'options'], // dados do grafico passado pelo componente que incluir esse grafico

  watch: {
    data: function (newVal) {
      // watch it
      this.renderChart(newVal.datacollection, this.options);
    },
  },

  mounted() {
    this.renderChart(this.data.datacollection, this.options);
  },

};
</script>
