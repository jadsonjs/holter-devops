
<script>

import { Bar } from 'vue-chartjs';

export default {

  name: 'BarChart',

  extends: Bar,

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
